package net.hasanguner.cartkata;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.LinkedList;
import java.util.List;

@Value
@EqualsAndHashCode(exclude = "parentCategory")
public class Category {
    private final @NonNull String title;
    private final Category parentCategory;

    public final List<Category> collectUpToParents() {
        return collectUp(this, new LinkedList<>());
    }

    private List<Category> collectUp(Category category, List<Category> accumulator) {
        if (category == null) return accumulator;
        accumulator.add(category);
        return collectUp(category.getParentCategory(), accumulator);

    }

    public static Category of(String title) {
        return new Category(title, null);
    }
}
