package net.hasanguner.cartkata;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Campaign extends Discount {
    private final Category category;
    private final Integer minimumQuantity;

    public Campaign(BigDecimal amount, DiscountType type, Category category, Integer minimumQuantity) {
        super(amount, type);
        Objects.requireNonNull(category, "Category must not be null!");
        Objects.requireNonNull(minimumQuantity, "MinimumQuantity must not be null!");
        this.category = category;
        this.minimumQuantity = minimumQuantity;
    }

}
