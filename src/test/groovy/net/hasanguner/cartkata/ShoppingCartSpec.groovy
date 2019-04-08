package net.hasanguner.cartkata

import spock.lang.Specification

class ShoppingCartSpec extends Specification {

    def "add item on cart should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        and:
        def furniture = Category.of("Furniture")
        def table = new Product("Table", BigDecimal.valueOf(1_000.0), furniture)
        when:
        cart.addItem(macBookPro, 1)
        cart.addItem(macBookPro, 2)
        cart.addItem(table, 1)
        then:
        def cartItems = cart.getCartItems()
        println "CART ITEMS : $cartItems"
        cartItems.size() == 2
        def products = cartItems.keySet()
        products.size() == 2
        products.containsAll([table, macBookPro])
        cartItems[table] == 1
        cartItems[macBookPro] == 3
    }

    def "apply campaign discounts on empty cart should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def campaign = new Campaign(10.0, DiscountType.RATE, computers, 1)
        when:
        def result = cart.applyDiscounts(campaign)
        then:
        !result
    }

    def "apply discounts attempt without meeting campaign requirements should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 1)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, computers, 2)
        when:
        def result = cart.applyDiscounts(tenPercentOff)
        then:
        !result
    }

    def "apply discounts attempt with multiple campaigns should select campaign requirements meeted one"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, computers, 3)
        def tenLiraDiscount = new Campaign(10.0, DiscountType.AMOUNT, computers, 2)
        when:
        def result = cart.applyDiscounts(tenPercentOff, tenLiraDiscount)
        then:
        result
        cart.getCampaignDiscount() == 10.0
    }

    def "apply discounts attempt with multiple campaigns should select most effective one"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, computers, 1)
        def tenLiraDiscount = new Campaign(10.0, DiscountType.AMOUNT, computers, 1)
        when:
        def result = cart.applyDiscounts(tenPercentOff, tenLiraDiscount)
        then:
        result
        cart.getCampaignDiscount() == 2_000.0
    }

    def "apply discount attempt on already campaign applied cart with less effective campaign should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def thousandLiraDiscount = new Campaign(1_000.0, DiscountType.AMOUNT, computers, 1)
        cart.applyDiscounts(thousandLiraDiscount)
        and:
        def tenLiraDiscount = new Campaign(10.0, DiscountType.AMOUNT, computers, 1)
        when:
        def result = cart.applyDiscounts(tenLiraDiscount)
        then:
        !result
        cart.getCampaignDiscount() == 1_000.0
    }

    def "apply discount attempt with already applied same campaign should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def thousandLiraDiscount = new Campaign(1_000.0, DiscountType.AMOUNT, computers, 1)
        cart.applyDiscounts(thousandLiraDiscount)
        when:
        def result = cart.applyDiscounts(thousandLiraDiscount)
        then:
        !result
        cart.getCampaignDiscount() == 1_000.0
    }

    def "apply discount on free of charge cart should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def tenThousandLiraDiscount = new Campaign(10_000.0, DiscountType.AMOUNT, computers, 1)
        cart.applyDiscounts(tenThousandLiraDiscount)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, computers, 1)
        when:
        def result = cart.applyDiscounts(tenPercentOff)
        then:
        !result
        cart.getCampaignDiscount() == 10_000.0
    }

    def "apply coupon on empty cart should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def coupon = new Coupon(100.0, DiscountType.AMOUNT, 1000.0)
        when:
        def result = cart.applyCoupon(coupon)
        then:
        !result
    }

    def "apply coupon attempt without meeting requirements should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 1)
        and:
        def coupon = new Coupon(2_000.0, DiscountType.AMOUNT, 11_000.0)
        when:
        def result = cart.applyCoupon(coupon)
        then:
        !result
    }

    def "apply coupon attempt on coupon requirements meeted cart should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def coupon = new Coupon(2_000.0, DiscountType.AMOUNT, 11_000.0)
        when:
        def result = cart.applyCoupon(coupon)
        then:
        result
        cart.getCouponDiscount() == 2_000.0
    }

    def "apply coupon attempt on already coupon applied cart with less effective coupon should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def twoThousandLiraOff = new Coupon(2_000.0, DiscountType.AMOUNT, 11_000.0)
        cart.applyCoupon(twoThousandLiraOff)
        and:
        def thousandLiraOff = new Coupon(1_000.0, DiscountType.AMOUNT, 11_000.0)
        when:
        def result = cart.applyCoupon(thousandLiraOff)
        then:
        !result
        cart.getCouponDiscount() == 2_000.0
    }

    def "apply coupon on free of charge cart should not succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        def computers = Category.of("computers")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), computers)
        cart.addItem(macBookPro, 2)
        and:
        def twentyThousandLiraOff = new Coupon(20_000.0, DiscountType.AMOUNT, 11_000.0)
        cart.applyCoupon(twentyThousandLiraOff)
        and:
        def thousandLiraOff = new Coupon(1_000.0, DiscountType.AMOUNT, 1.0)
        when:
        def result = cart.applyCoupon(thousandLiraOff)
        then:
        !result
        cart.getCouponDiscount() == 20_000.0
    }

    def "get number of distinct categories should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        def refrigerator = new Product("Bosh Refrigerator", BigDecimal.valueOf(5_000.0), electronics)
        and:
        def furniture = Category.of("Furniture")
        def table = new Product("Table", BigDecimal.valueOf(1_000.0), furniture)
        when:
        cart.addItem(refrigerator, 1)
        cart.addItem(macBookPro, 2)
        cart.addItem(table, 1)
        def result = cart.getNumberOfDistinctCategories()
        then:
        result == 2
    }

    def "get number of distinct products should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        def refrigerator = new Product("Bosh Refrigerator", BigDecimal.valueOf(5_000.0), electronics)
        and:
        def furniture = Category.of("Furniture")
        def table = new Product("Table", BigDecimal.valueOf(1_000.0), furniture)
        when:
        cart.addItem(refrigerator, 1)
        cart.addItem(macBookPro, 2)
        cart.addItem(table, 1)
        def result = cart.getNumberOfDistinctProducts()
        then:
        result == 3
    }

    def "get total amount after discounts on over discounted cart should return 0"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        and:
        cart.addItem(macBookPro, 1)
        and:
        def nonRealisticDiscount = new Campaign(10_000.0, DiscountType.AMOUNT, electronics, 1)
        cart.applyDiscounts(nonRealisticDiscount)
        when:
        def totalAmount = cart.getTotalAmountAfterDiscounts()
        then:
        totalAmount == 0.0
    }

    def "get total amount after discounts should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        and:
        cart.addItem(macBookPro, 2)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, electronics, 1)
        cart.applyDiscounts(tenPercentOff)
        when:
        def totalAmount = cart.getTotalAmountAfterDiscounts()
        then:
        totalAmount == 18_000.0
    }

    def "get campaign discount should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        and:
        cart.addItem(macBookPro, 2)
        and:
        def tenPercentOff = new Campaign(10.0, DiscountType.RATE, electronics, 1)
        cart.applyDiscounts(tenPercentOff)
        when:
        def discountAmount = cart.getCampaignDiscount()
        then:
        discountAmount == 2_000.0
    }

    def "get coupon discount should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        and:
        cart.addItem(macBookPro, 2)
        and:
        def thousandLiraOff = new Coupon(1_000.0, DiscountType.AMOUNT, 10_000.0)
        cart.applyCoupon(thousandLiraOff)
        when:
        def discountAmount = cart.getCouponDiscount()
        then:
        discountAmount == 1_000.0
    }

    def "get delivery cost should use delivery cost calculator"() {
        given:
        def calculator = Mock(DeliveryCostCalculator)
        def cart = new ShoppingCart(calculator)
        and:
        def electronics = Category.of("electronics")
        def macBookPro = new Product("MacBookPro", BigDecimal.valueOf(10_000.0), electronics)
        cart.addItem(macBookPro, 2)
        and:
        def thousandLiraOff = new Coupon(1_000.0, DiscountType.AMOUNT, 10_000.0)
        cart.applyCoupon(thousandLiraOff)
        when:
        def cost = cart.getDeliveryCost()
        then:
        1 * calculator.calculateFor(cart) >> 150.0
        cost == 150.0
    }

    def "print should succeed"() {
        given:
        def calculator = Mock(DeliveryCostCalculator) {
            calculateFor(_) >> 200.0
        }
        def cart = new ShoppingCart(calculator)
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
        expect:
        cart.print()
        //succeed
    }

}