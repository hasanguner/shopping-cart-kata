package net.hasanguner.cartkata;

import java.math.BigDecimal;
import java.util.Map;

public interface Cart {

    void addItem(Product product, int quantity);

    Map<Product, Integer> getCartItems();

    boolean applyDiscounts(Campaign... campaigns);

    boolean applyCoupon(Coupon coupon);

    Long getNumberOfDistinctCategories();

    Long getNumberOfDistinctProducts();

    BigDecimal getTotalAmountAfterDiscounts();

    BigDecimal getCampaignDiscount();

    BigDecimal getCouponDiscount();

    BigDecimal getDeliveryCost();

    void print();
}
