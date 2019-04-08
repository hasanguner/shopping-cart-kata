package net.hasanguner.cartkata

import spock.lang.Specification

class CategorySpec extends Specification {

    def "collecting up to parent categories on non-parent associated category"() {
        given:
        def electronics = Category.of("Electronics")
        when:
        def categories = electronics.collectUpToParents()
        then:
        println "CATEGORIES : $categories"
        !categories.isEmpty()
        categories.size() == 1
        categories[0] == electronics
    }

    def "collecting up to parent categories on a sub-category"() {
        given:
        def electronics = Category.of("Electronics")
        def computers = new Category("Computers", electronics)
        def macBooks = new Category("MacBooks", computers)
        when:
        def categories = macBooks.collectUpToParents()
        then:
        println "CATEGORIES : $categories"
        !categories.isEmpty()
        categories.size() == 3
        categories.containsAll([electronics, computers, macBooks])
    }

}
