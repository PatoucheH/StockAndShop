package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.UuidBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Recipe extends UuidBaseEntity {

    @Column(nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<RecipeProduct> recipeProducts = new ArrayList<>();

    // @OrderColumn persists the list index to step_order so steps are always retrieved in the right sequence
    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step", columnDefinition = "TEXT")
    @OrderColumn(name = "step_order")
    private List<String> steps = new ArrayList<>();

    @Column(nullable = false)
    private int timing;

    @Column(nullable = true)
    private Double score;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_tags",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    public Recipe(String title,
                  List<RecipeProduct> recipeProducts,
                  List<String> steps,
                  int timing,
                  List<Tag> tags) {
        this.title = title;
        this.recipeProducts = recipeProducts;
        this.steps = steps;
        this.timing = timing;
        this.tags = tags;
    }

}
