package net.hasanguner.cartkata;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class Product {
    private final @NonNull String title;
    private final @NonNull BigDecimal price;
    private final @NonNull Category category;
}
