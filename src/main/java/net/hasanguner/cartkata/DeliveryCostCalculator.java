package net.hasanguner.cartkata;

import java.math.BigDecimal;

public interface DeliveryCostCalculator {

    BigDecimal calculateFor(Cart cart);
}
