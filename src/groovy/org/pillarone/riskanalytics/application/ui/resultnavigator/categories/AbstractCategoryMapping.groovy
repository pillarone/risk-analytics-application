package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * User: martin.melchior
 */
class AbstractCategoryMapping implements ICategoryMapping {

    Map<String, ICategoryMatcher> matcherMap = [:]

    List<String> getCategories() {
        return matcherMap.keySet().asList()
    }

    String getCategoryMember(String category, String path) {
        return matcherMap[category]?.getMatch(path)
    }
}
