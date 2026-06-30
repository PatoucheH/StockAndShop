package be.stockandshopbackend.config;

import be.stockandshopbackend.dal.repositories.TagRepository;
import be.stockandshopbackend.dl.entities.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class TagSeeder implements ApplicationRunner {

    private final TagRepository tagRepository;

    private static final List<String> PREDEFINED_TAGS = List.of(
            "Végétarien", "Vegan", "Sans gluten", "Sans lactose", "Riche en protéines",
            "Petit-déjeuner", "Déjeuner", "Dîner", "Dessert", "Apéritif", "Snack",
            "Rapide", "Long",
            "Facile", "Intermédiaire", "Difficile",
            "Italienne", "Asiatique", "Française", "Méditerranéenne", "Mexicaine",
            "Économique", "Réchauffable", "Convivial", "Léger", "Hivernal", "Estival"
    );

    @Override
    public void run(ApplicationArguments args) {
        if (tagRepository.count() > 0) return;
        PREDEFINED_TAGS.stream()
                .map(Tag::new)
                .forEach(tagRepository::save);
    }
}
