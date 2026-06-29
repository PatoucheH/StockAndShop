package be.stockandshopbackend.bll.services.recipe;

import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dal.repositories.ProductRepository;
import be.stockandshopbackend.dal.repositories.RecipeRepository;
import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.exceptions.NotFoundException;
import be.stockandshopbackend.exceptions.RecipeNotPossibleException;
import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final AnthropicClient anthropicClient;
    private final RecipeRepository recipeRepository;
    private final HomeRepository homeRepository;
    private final ProductRepository productRepository;

    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    @Transactional
    public List<Recipe> getSuggestions(UUID homeId) {
        Home home = homeRepository.findById(homeId).orElseThrow(
                () -> new NotFoundException("Home not found with id: " + homeId)
        );
        List<String> stockProductNames = home.getStocks().stream()
                .map(s -> s.getProduct().getName())
                .toList();
        return getSuggestionsWithProducts(homeId, stockProductNames);
    }

    /**
     * Returns existing recipes whose every ingredient is covered by the provided product list.
     * Falls back to AI generation when no existing recipe matches — the generated recipe is persisted
     * and counts toward future suggestions.
     */
    @Transactional
    public List<Recipe> getSuggestionsWithProducts(UUID homeId, List<String> productNames) {
        Set<String> filter = new HashSet<>(productNames);
        List<Recipe> matching = recipeRepository.findAll().stream()
                .filter(recipe -> recipe.getRecipeProducts().stream()
                        .allMatch(rp -> filter.contains(rp.getProduct().getName()))
                )
                .toList();
        if (!matching.isEmpty()) {
            return matching;
        }
        return List.of(generateAndSave(homeId));
    }

    @Transactional
    public Recipe generateAndSaveWithProduct(List<ProductStockHome> products) {
        return generateRecipe(products);
    }

    @Transactional
    public Recipe generateAndSave(UUID homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new NotFoundException(
                        "Home not found with id: " + homeId));
        if (home.getStocks().isEmpty()) {
            throw new IllegalStateException(
                    "Stock of this home: " + homeId + " is empty");
        }
        return generateRecipe(home.getStocks());
    }

    private Recipe generateRecipe(List<ProductStockHome> products) {
        List<String> existingTitles = recipeRepository.findAll().stream()
                .map(Recipe::getTitle)
                .toList();
        String claudeResponse = callClaude(buildPrompt(products, existingTitles));
        if (claudeResponse.trim().startsWith("IMPOSSIBLE")) {
            String reason = claudeResponse.trim().replaceFirst("^IMPOSSIBLE[:\\s]*", "").trim();
            throw new RecipeNotPossibleException(reason.isEmpty()
                    ? "Les ingrédients disponibles ne permettent pas de créer une recette cohérente."
                    : reason);
        }
        Recipe recipe = parseResponse(claudeResponse);
        return recipeRepository.save(recipe);
    }

    // Claude is told it may use these in recipe steps but must NEVER list them as stock INGREDIENTS,
    // so the parsed ingredient section only contains items that are actually tracked in the home stock.
    private static final String PANTRY_STAPLES =
            "eau, eau minérale, sel, sel fin, poivre, poivre noir, poivre blanc, " +
            "huile d'olive, huile de tournesol, huile végétale, vinaigre, " +
            "beurre, sucre, farine, levure chimique, bicarbonate de soude";

    private String buildPrompt(List<ProductStockHome> stocks, List<String> existingTitles) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a head chef. Here are the products currently in the pantry:\n\n");
        stocks.forEach(stock -> sb.append("- ")
                .append(stock.getProduct().getName())
                .append(": ").append(stock.getQuantity())
                .append(" ").append(stock.getProduct().getUnity().name().toLowerCase())
                .append("\n"));
        // Passing existing titles to Claude to avoid generating duplicates
        if (!existingTitles.isEmpty()) {
            sb.append("\nThe following recipes already exist — do NOT suggest any of them:\n");
            existingTitles.forEach(title -> sb.append("- ").append(title).append("\n"));
        }
        sb.append("""
                  The following are universal household staples that everyone always has at home.
                  You may use them freely in the recipe steps, but NEVER list them in the INGREDIENTS section:
                  """)
          .append(PANTRY_STAPLES).append("\n");
        sb.append("""
                  Suggest a NEW recipe using ONLY the pantry products listed above.
                  Rules:
                  - Only use ingredients from the provided stock list (staples are allowed in steps but not in INGREDIENTS).
                  - A recipe must be culinarily coherent, realistic and appetizing.
                  - If the available products absolutely cannot produce a coherent and appetizing recipe (e.g. random
                    unrelated products, only drinks, incoherent combinations), do NOT force a recipe. Instead, reply
                    with exactly: IMPOSSIBLE: <brief reason in French>. Nothing else.

                  Please reply ONLY in this format, in FRENCH, without any additional text:

                  TITLE: name of the recipe
                  INGREDIENTS:
                  exact_product_name:quantity_as_integer
                  STEPS:
                  Faire revenir les oignons dans l'huile à feu moyen pendant 5 minutes.
                  Ajouter les tomates et laisser mijoter 10 minutes.
                  servir chaud.
                  TIMING: <integer number of minutes>
                  TAGS: tag1, tag2, tag3

                  Important: product names must match EXACTLY the names provided in the stock list.
                  Do NOT use brackets [] around any text.
                  Do NOT list staple ingredients (water, salt, pepper, oil, butter, flour, sugar, vinegar) in INGREDIENTS.
                 \s""");
        return sb.toString();
    }

    private String callClaude(String prompt) {
        Message message = anthropicClient.messages().create(
                MessageCreateParams.builder()
                        .model(Model.CLAUDE_HAIKU_4_5)
                        .maxTokens(2048L)
                        .addUserMessage(prompt)
                        .build()
        );
        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());
    }

    private Recipe parseResponse(String claudeResponse) {
        String[] sections = claudeResponse.split("\n");
        String title = "";
        List<RecipeProduct> products = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        int timing = 0;
        List<String> tags = new ArrayList<>();
        String currentSection = "";
        for (String line : sections) {
            line = line.trim();
            if (line.startsWith("TITLE:")) {
                title = line.replace("TITLE:", "").trim();
            } else if (line.equals("INGREDIENTS:")) {
                currentSection = "INGREDIENTS";
            } else if (line.equals("STEPS:")) {
                currentSection = "STEPS";
            } else if (line.startsWith("TIMING:")) {
                currentSection = "TIMING";
                try {
                    timing = Integer.parseInt(line.replace("TIMING:", "").trim());
                } catch (NumberFormatException ignored) {}
            } else if (line.startsWith("TAGS:")) {
                currentSection = "TAGS";
                String rawTags = line.replace("TAGS:", "").trim();
                if (!rawTags.isEmpty()) {
                    for (String tag : rawTags.split(",")) {
                        String t = tag.trim();
                        if (!t.isEmpty()) tags.add(t);
                    }
                }
            } else if (!line.isEmpty()) {
                switch (currentSection) {
                    case "INGREDIENTS" -> {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            productRepository.findByName(parts[0].trim()).ifPresent(product -> {
                                try {
                                    products.add(new RecipeProduct(product, Integer.parseInt(parts[1].trim())));
                                } catch (NumberFormatException ignored) {
                                }
                            });
                        }
                    }
                    case "STEPS" ->
                        // Claude occasionally wraps step text in brackets despite instructions — strip them
                            steps.add(line.replaceAll("^\\[|]$", "").trim());
                    case "TAGS" -> {
                        for (String tag : line.split(",")) {
                            String t = tag.trim();
                            if (!t.isEmpty()) tags.add(t);
                        }
                    }
                }
            }
        }
        return new Recipe(title, products, steps, timing, tags);
    }
}