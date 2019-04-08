package net.hasanguner.cartkata;

import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public enum DiscountType {
    RATE {
        @Override
        public BigDecimal calculate(@NonNull Map<Product, Integer> productQuantityMap, @NonNull BigDecimal discountAmount) {
            return productQuantityMap.entrySet()
                    .stream()
                    .map(it -> it.getKey().getPrice().multiply(BigDecimal.valueOf(it.getValue())))
                    .map(it -> it.multiply(discountAmount))
                    .map(it -> it.divide(HUNDRED, TRY_SCALE, RoundingMode.CEILING))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    },
    AMOUNT {
        @Override
        public BigDecimal calculate(Map<Product, Integer> productQuantityMap, @NonNull BigDecimal discountAmount) {
            return discountAmount;
        }
    };

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int TRY_SCALE = 2;

    public abstract BigDecimal calculate(Map<Product, Integer> productQuantityMap, BigDecimal discountAmount);
}
