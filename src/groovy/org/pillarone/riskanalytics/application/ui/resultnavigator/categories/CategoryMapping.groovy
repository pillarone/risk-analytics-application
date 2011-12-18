package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class CategoryMapping {

    Map<String, ICategoryMatcher> matcherMap = [:]
    List<ICategoryChangeListener> categoryChangeListeners = []

    void addCategoryChangeListener(ICategoryChangeListener listener) {
        if (!categoryChangeListeners.contains(listener)) {
            categoryChangeListeners.add listener
        }
    }

    void removeCategoryChangeListener(ICategoryChangeListener listener) {
        if (categoryChangeListeners.contains(listener)) {
            categoryChangeListeners.remove listener
        }
    }

    int getNumberOfCategories() {
        return matcherMap.size()
    }
    
    List<String> getCategories() {
        return matcherMap.keySet().asList()
    }

    void addCategory(String category) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,null)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryAdded(category)
            }
        }
    }

    void addCategory(String category, ICategoryMatcher matcher) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,matcher)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryAdded(category)
            }
        }
    }

    void setCategory(String category, ICategoryMatcher matcher) {
        matcherMap.put(category,matcher)
    }

    boolean hasCategory(String category) {
        return matcherMap.containsKey(category)
    }

    boolean removeCategory(String category) {
        if (matcherMap.containsKey(category)) {
            ICategoryMatcher matcher = matcherMap.remove(category)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryRemoved(category)
            }
            return matcher != null
        }
        return false
    }

    void removeAllCategories() {
        for (String category : matcherMap.keySet()) {
            removeCategory(category)
        }
    }

    ICategoryMatcher getCategoryMatcher(String category) {
        return matcherMap[category]
    }
    
    String getCategoryMember(String category, String path) {
        return matcherMap[category]?.getMatch(path)
    }
}
