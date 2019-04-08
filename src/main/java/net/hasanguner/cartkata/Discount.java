package net.hasanguner.cartkata;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Map;

@Data
public abstract class Discount {
    private final @NonNull BigDecimal amount;
    private final @NonNull DiscountType type;

    public final BigDecimal calculate(@NonNull Map<Product, Integer> productQuantityMap) {
        return type.calculate(productQuantityMap, amount);
    }

}
