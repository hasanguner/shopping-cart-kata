package net.hasanguner.cartkata

import spock.lang.Specification

class ShoppingCartIntegrationSpec extends Specification {

    def "shopping cart should operate successfully"() {
        given:
        def costPerDelivery = 14.99
        def costPerProduct = 1.99
        def fixedCost = 2.99
        DeliveryCostCalculator deliveryCostCalculator = new DefaultDeliveryCostCalculator(costPerDelivery, costPerProduct, fixedCost)
        Cart cart = new ShoppingCart(deliveryCostCalculator)
        and:
        def electronics = Category.of("electronics")
        def computers = new Category("Computers", electronics)
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        def refrigerator = new Product("Bosh Refrigerator", BigDecimal.valueOf(5_000.0), electronics)
        def furniture = Category.of("Furniture")
        def table = new Product("Table", BigDecimal.valueOf(1_000.0), furniture)
        and:
        cart.addItem(refrigerator, 1)
        cart.addItem(macBookPro, 2)
        cart.addItem(table, 1)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, computers, 1)
        cart.applyDiscounts(tenPercentOff)
        and:
        def thousandLiraOff = new Coupon(1_000.0, DiscountType.AMOUNT, 15_000.0)
        cart.applyCoupon(thousandLiraOff)
        when:
        def distinctCategories = cart.getNumberOfDistinctCategories()
        def distinctProducts = cart.getNumberOfDistinctProducts()
        def totalAmount = cart.getTotalAmountAfterDiscounts()
        def campaignDiscount = cart.getCampaignDiscount()
        def couponDiscount = cart.getCouponDiscount()
        def deliveryCost = cart.getDeliveryCost()
        cart.print()
        then:
        println "\nDISTINCT CATEGORIES : $distinctCategories"
        distinctCategories == 3
        println "DISTINCT PRODUCTS : $distinctProducts"
        distinctProducts == 3
        println "TOTAL AMOUNT: $totalAmount"
        totalAmount == 23_000.0
        println "CAMPAIGN DISCOUNT : $campaignDiscount"
        campaignDiscount == 2_000.0
        println "COUPON DISCOUNT : $couponDiscount"
        couponDiscount == 1_000.0
        println "DELIVERY COST: $deliveryCost"
        deliveryCost == 53.93
    }

}