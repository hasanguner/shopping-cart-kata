package net.hasanguner.cartkata;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Coupon extends Discount {
    private final BigDecimal minimumAmount;

    public Coupon(BigDecimal amount, DiscountType type, BigDecimal minimumAmount) {
        super(amount, type);
        this.minimumAmount = minimumAmount;
    }
}


