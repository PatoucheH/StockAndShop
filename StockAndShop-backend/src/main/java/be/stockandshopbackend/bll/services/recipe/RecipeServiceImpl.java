package be.stockandshopbackend.bll.services.recipe;

import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dal.repositories.ProductRepository;
import be.stockandshopbackend.dal.repositories.RecipeRepository;
import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.exceptions.NotFoundException;
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
        Set<String> stockProductNames = home.getStocks().stream()
                .map(s -> s.getProduct().getName())
                .collect(Collectors.toSet());
        List<Recipe> matching = recipeRepository.findAll().stream()
                .filter(recipe -> recipe.getRecipeProducts().stream()
                        .allMatch(rp -> stockProductNames.contains(rp.getProduct().getName())))
                .toList();
        if (!matching.isEmpty()) {
            return matching;
        }
        return List.of(generateAndSave(homeId));
    }

    @Transactional
    public Recipe generateAndSave(UUID homeId) {
        Home home = homeRepository.findById(homeId).orElseThrow(
                () -> new NotFoundException("Home not found with id: " + homeId)
        );
        if (home.getStocks().isEmpty()) {
            throw new IllegalStateException("Stock of this home: " + homeId + " is empty");
        }
        List<String> existingTitles = recipeRepository.findAll().stream()
                .map(Recipe::getTitle)
                .toList();
        String claudeResponse = callClaude(buildPrompt(home.getStocks(), existingTitles));
        Recipe recipe = parseResponse(claudeResponse);
        return recipeRepository.save(recipe);
    }

    private String buildPrompt(List<ProductStockHome> stocks, List<String> existingTitles) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a head chef. Here are the products currently in stock:\n\n");
        stocks.forEach(stock -> sb.append("- ")
                .append(stock.getProduct().getName())
                .append(": ").append(stock.getQuantity())
                .append(" ").append(stock.getProduct().getUnity().name().toLowerCase())
                .append("\n"));
        if (!existingTitles.isEmpty()) {
            sb.append("\nThe following recipes already exist — do NOT suggest any of them:\n");
            existingTitles.forEach(title -> sb.append("- ").append(title).append("\n"));
        }
        sb.append("""
   
                  Suggest a NEW recipe using these ingredients.
                  Please reply ONLY in this format, in FRENCH, without any additional text:
   
                  TITLE: [name of the recipe]
                  INGREDIENTS:
                  [exact_product_name]:[quantity as a whole number]
                  STEPS:
                  [step 1]
                  [step 2]
                  [etc.]
   
                  Important: product names must match EXACTLY the names provided in the stock list.
                  """);
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
        String currentSection = "";
        for (String line : sections) {
            line = line.trim();
            if (line.startsWith("TITLE:")) {
                title = line.replace("TITLE:", "").trim();
            } else if (line.equals("INGREDIENTS:")) {
                currentSection = "INGREDIENTS";
            } else if (line.equals("STEPS:")) {
                currentSection = "STEPS";
            } else if (!line.isEmpty()) {
                if (currentSection.equals("INGREDIENTS")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        productRepository.findByName(parts[0].trim()).ifPresent(product -> {
                            try {
                                products.add(new RecipeProduct(product, Integer.parseInt(parts[1].trim())));
                            } catch (NumberFormatException ignored) {}
                        });
                    }
                } else if (currentSection.equals("STEPS")) {
                    steps.add(line);
                }
            }
        }
        return new Recipe(title, products, steps);
    }
}