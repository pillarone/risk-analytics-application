package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class AbstractCategoryMapping implements ICategoryMapping {

    Map<String, ICategoryMatcher> matcherMap = [:]

    int getNumberOfCategories() {
        return matcherMap.size()
    }
    
    List<String> getCategories() {
        return matcherMap.keySet().asList()
    }

    void addCategory(String category) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,null)
        }
    }

    void addCategory(String category, ICategoryMatcher matcher) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,matcher)
        }
    }

    void setCategory(String category, ICategoryMatcher matcher) {
        matcherMap.put(category,matcher)
    }

    boolean hasCategory(String category) {
        return matcherMap.containsKey(category)
    }

    boolean removeCategory(String category) {
        ICategoryMatcher matcher = matcherMap.remove(category)
        return matcher != null
    }

    void removeAllCategories() {
        matcherMap.clear()
    }

    ICategoryMatcher getMatcher(String category) {
        return matcherMap[category]
    }
    
    String getCategoryMember(String category, String path) {
        return matcherMap[category]?.getMatch(path)
    }
}
