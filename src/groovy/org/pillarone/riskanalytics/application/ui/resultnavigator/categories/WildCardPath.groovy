package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import groovy.text.Template
import groovy.text.GStringTemplateEngine

/**
 * @author martin.melchior
 */
class WildCardPath {

    Template template
    String templatePath

    // map category <-> wildcard values
    Map<String, List<String>> wildCardsMap

    WildCardPath(String spec, List<String> wildCards) {
        templatePath = spec
        GStringTemplateEngine engine = new GStringTemplateEngine()
        this.template = engine.createTemplate(spec)
        this.wildCardsMap = [:]
        for (String wildCard : wildCards) {
            wildCardsMap[wildCard] = []
        }
    }

    /**
     * Method to return the wild card available for this template
     * @return
     */
    List<String> getWildCards() {
        return wildCardsMap.keySet().asList()
    }

    /**
     * Method to return the possible values for a given wild card element defined for this wild card path.
     * @param category
     * @return
     */
    List<String> getWildCardValues(String category) {
        return wildCardsMap[category]
    }

    /**
     * Method to set the possible values for a given wild card element defined for this wild card path.
     * @param category
     * @return
     */
    void addWildCardValue(String category, String value) {
        if (!wildCardsMap[category].contains(value)) {
            this.wildCardsMap[category].add(value)
        }
    }

    /**
     * Compose from the wild card path a specific path by entering the given values
     * into the wild card elements included in this wild card path.
     * @param wildCardValues
     * @return
     */
    String getSpecificPath(Map<String,String> wildCardValues) {
        return template.make(wildCardValues)
    }
}
