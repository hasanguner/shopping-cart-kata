package net.hasanguner.cartkata

import spock.lang.Specification

class DiscountTypeSpec extends Specification {

    def "calculation on amount discount type should return discount amount directly"() {
        given:
        def discountType = DiscountType.AMOUNT
        def discountAmount = 100.0
        def cartItems = Mock(Map)
        when:
        def discountOnCart = discountType.calculate(cartItems, discountAmount)
        then:
        discountOnCart == discountAmount
    }

    def "calculation on rate discount type should return discount amount based on cart items"() {
        given:
        def discountType = DiscountType.RATE
        def discountAmount = 10.0
        def cartItems = [:]
        def computers = Category.of("Computers")
        def macBookPro = new Product("MacBook Pro", 10_000.0, computers)
        cartItems[macBookPro] = 2
        when:
        def discountOnCart = discountType.calculate(cartItems, discountAmount)
        then:
        discountOnCart == 2_000
    }

}
