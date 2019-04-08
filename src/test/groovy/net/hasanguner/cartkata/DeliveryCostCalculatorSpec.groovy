package net.hasanguner.cartkata

import spock.lang.Specification

class DeliveryCostCalculatorSpec extends Specification {

    def "delivery cost calculation should succeed"() {
        given:
        def costPerDelivery = 14.99
        def costPerProduct = 1.99
        def fixedCost = 2.99
        def deliveryCostCalculator = new DefaultDeliveryCostCalculator(costPerDelivery, costPerProduct, fixedCost)
        def cart = Mock(Cart)
        when:
        def deliveryCost = deliveryCostCalculator.calculateFor(cart)
        then:
        println "DELIVERY COST : $deliveryCost"
        1 * cart.getNumberOfDistinctCategories() >> 3
        1 * cart.getNumberOfDistinctProducts() >> 4
        55.92 == deliveryCost
    }

}
