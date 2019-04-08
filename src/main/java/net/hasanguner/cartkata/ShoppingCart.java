package net.hasanguner.cartkata;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.val;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Map.Entry;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public final class ShoppingCart implements Cart {

    private final Object cartLock = new Object();
    private final @NonNull DeliveryCostCalculator costCalculator;
    private Map<Product, Integer> items = new ConcurrentHashMap<>();
    private BigDecimal campaignDiscount = BigDecimal.ZERO;
    private BigDecimal couponDiscount = BigDecimal.ZERO;
    private BigDecimal purchaseAmount = BigDecimal.ZERO;

    @Override
    @Synchronized("cartLock")
    public final void addItem(@NonNull Product product, int quantity) {
        validateQuantity(quantity);
        int newQuantity = items.getOrDefault(product, 0) + quantity;
        items.put(product, newQuantity);
        val purchaseAddition = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        purchaseAmount = purchaseAmount.add(purchaseAddition);
    }

    @Override
    public Map<Product, Integer> getCartItems() {
        return items.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @Override
    @Synchronized("cartLock")
    public final boolean applyDiscounts(@NonNull Campaign... campaigns) {
        if (isCartInFreeOfCharge()) return false;
        val calculatedDiscount = Arrays.stream(campaigns)
                .map(this::discountBy)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        if (calculatedDiscount.compareTo(campaignDiscount) > 0) {
            campaignDiscount = calculatedDiscount;
            return true;
        }
        return false;
    }

    @Override
    @Synchronized("cartLock")
    public final boolean applyCoupon(@NonNull Coupon coupon) {
        if (isCartInFreeOfCharge()) return false;
        val calculatedDiscount = Optional.of(coupon)
                .filter(this::isApplicable)
                .map(it -> it.calculate(Collections.emptyMap()))
                .orElse(BigDecimal.ZERO);

        if (calculatedDiscount.compareTo(couponDiscount) > 0) {
            couponDiscount = calculatedDiscount;
            return true;
        }
        return false;
    }

    @Override
    public Long getNumberOfDistinctCategories() {
        return items.keySet()
                .stream()
                .map(Product::getCategory)
                .distinct()
                .count();
    }

    @Override
    public Long getNumberOfDistinctProducts() {
        return items.keySet()
                .stream()
                .distinct()
                .count();
    }

    @Override
    @Synchronized("cartLock")
    public final BigDecimal getTotalAmountAfterDiscounts() {
        return Optional.of(purchaseAmount.subtract(campaignDiscount).subtract(couponDiscount))
                .filter(it -> it.compareTo(BigDecimal.ZERO) > 0)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Synchronized("cartLock")
    public final BigDecimal getCampaignDiscount() {
        return campaignDiscount;
    }

    @Override
    @Synchronized("cartLock")
    public final BigDecimal getCouponDiscount() {
        return couponDiscount;
    }

    @Override
    public final BigDecimal getDeliveryCost() {
        return costCalculator.calculateFor(this);
    }

    @Override
    public void print() {
        val separator = "\n" + String.join("", Collections.nCopies(120, "-"));
        System.out.println(separator);
        System.out.format("%25s | %25s | %25s | %25s%11s", "Category Name", "Product Name", "Quantity", "Unit Price (TRY)", "|");
        System.out.print(separator);
        items.entrySet()
                .stream()
                .sorted(Comparator.comparing(it -> it.getKey().getCategory().getTitle()))
                .forEach(it -> System.out.format("\n%25s | %25s | %25d | %25s%11s",
                        it.getKey().getCategory().getTitle(),
                        it.getKey().getTitle(),
                        it.getValue(),
                        it.getKey().getPrice(),
                        "|")
                );
        System.out.println(separator);
        System.out.format("%25s | %25s |", "Total Amount (TRY)", "Delivery Cost (TRY)");
        System.out.println();
        System.out.format("%25s | %25s |", getTotalAmountAfterDiscounts(), getDeliveryCost());
        System.out.println();
    }

    private boolean isCartInFreeOfCharge() {
        return getTotalAmountAfterDiscounts().compareTo(BigDecimal.ZERO) == 0;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        }
    }

    private boolean isApplicable(Coupon coupon) {
        return purchaseAmount.compareTo(coupon.getMinimumAmount()) >= 0;
    }

    private BigDecimal discountBy(Campaign campaign) {
        return Optional.of(findApplicableItems(campaign))
                .filter(it -> !it.isEmpty())
                .map(campaign::calculate)
                .orElse(BigDecimal.ZERO);
    }

    private Map<Product, Integer> findApplicableItems(Campaign campaign) {
        return items.entrySet()
                .stream()
                .filter(it -> it.getKey().getCategory().collectUpToParents().contains(campaign.getCategory()))
                .filter(it -> it.getValue() >= campaign.getMinimumQuantity())
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

}
