package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
public enum Matcher {
    BY_SINGLE_LIST_VALUE("Match Single Value"),
    BY_REGEX("Match Regex"),
    BY_ENCLOSING("Enclosing Match"),
    BY_ENDING("Match Ending"),
    BY_CONDITION("Match Condition"),
    OR("OR"),
    AND("AND"),
    BY_CROSS_SECTION("Cross Section")

    String name
    Matcher(String name) {
        this.name = name
    }

    String toString() {
        return name
    }
}