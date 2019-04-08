package net.hasanguner.cartkata;

import lombok.Data;
import lombok.NonNull;
import lombok.val;

import java.math.BigDecimal;

@Data
public class DefaultDeliveryCostCalculator implements DeliveryCostCalculator {

    private final @NonNull BigDecimal costPerDelivery;
    private final @NonNull BigDecimal costPerProduct;
    private final @NonNull BigDecimal fixedCost;

    @Override
    public BigDecimal calculateFor(@NonNull Cart cart) {
        val numberOfDeliveries = BigDecimal.valueOf(cart.getNumberOfDistinctCategories());
        val numberOfProducts = BigDecimal.valueOf(cart.getNumberOfDistinctProducts());
        return costPerDelivery.multiply(numberOfDeliveries)
                .add(costPerProduct.multiply(numberOfProducts))
                .add(fixedCost);
    }
}
