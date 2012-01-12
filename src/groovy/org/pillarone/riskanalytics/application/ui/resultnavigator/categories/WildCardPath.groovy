package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import groovy.text.Template
import groovy.text.GStringTemplateEngine
import java.util.HashMap.Entry

/**
 * @author martin.melchior
 */
class WildCardPath {

    Template template
    String templatePath

    // map category <-> wildcard values
    Map<String, List<String>> wildCardsMap
    List<String> pathWildCards

    void setWildCardPath(String spec, List<String> pathWildCards) {
        templatePath = spec
        GStringTemplateEngine engine = new GStringTemplateEngine()
        this.template = engine.createTemplate(spec)
        this.pathWildCards = pathWildCards
        this.wildCardsMap = [:]
        for (String wildCard : pathWildCards) {
            wildCardsMap[wildCard] = []
        }
    }

    /**
     * Method to return the wild card available for this template
     * @return
     */
    List<String> getPathWildCards() {
        return pathWildCards
    }

    /**
     * Method to return the wild card available for this template
     * @return
     */
    List<String> getAllWildCards() {
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
        if (!wildCardsMap.containsKey(category)) {
            wildCardsMap[category] = []
        }
        if (!wildCardsMap[category].contains(value)) {
            this.wildCardsMap[category].add(value)
        }
    }

    /**
     * Method to set the possible values for a given wild card element defined for this wild card path.
     * @param category
     * @return
     */
    void addPathWildCardValue(String category, String value) {
        if (pathWildCards.indexOf(category)<0) {
            pathWildCards.add(category)
        }
        addWildCardValue(category, value)
    }

    /**
     * Compose from the wild card path a specific path by entering the given values
     * into the wild card elements included in this wild card path.
     * @param wildCardValues
     * @return
     */
    String getSpecificPath(Map<String,String> wildCardValues) {
        Map map = [:]
        for (String wc  : pathWildCards) {
            if (wildCardValues.containsKey(wc)) {
                map[wc] = wildCardValues[wc]
            }
        }
        return template.make(map)
    }
}
