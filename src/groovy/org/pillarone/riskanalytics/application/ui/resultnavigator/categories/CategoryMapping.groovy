package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import java.util.Map.Entry
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement

/**
 * @author martin.melchior
 */
class CategoryMapping {

    Map<String, ICategoryResolver> matcherMap = [:]
    Map<String, WildCardPath> wildCardPaths = [:]

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

    void addCategory(String category, ICategoryResolver matcher) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,matcher)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryAdded(category)
            }
        }
    }

    void setCategory(String category, ICategoryResolver matcher) {
        matcherMap.put(category,matcher)
    }

    boolean hasCategory(String category) {
        return matcherMap.containsKey(category)
    }

    boolean removeCategory(String category) {
        if (matcherMap.containsKey(category)) {
            ICategoryResolver matcher = matcherMap.remove(category)
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

    ICategoryResolver getCategoryMatcher(String category) {
        return matcherMap[category]
    }

    String getValueForCategory(OutputElement element, String category) {
        return matcherMap[category]?.getResolvedValue(element)
    }

    void addCategoriesToElement(OutputElement element) {
        for (Entry<String, ICategoryResolver> entry in matcherMap.entrySet()) {
            Object value = entry.value?.getResolvedValue(element)
            if (value && !element.categoryMap.keySet().contains(entry.key)) {
                element.addCategoryValue(entry.key,value)
            }
        }
    }

    List<String> createTemplatePath(OutputElement element) {
        List<String> categories = []
        for (Entry<String, ICategoryResolver> entry in matcherMap.entrySet()) {
            String category = entry.key
            ICategoryResolver resolver = entry.value
            if (resolver.createTemplatePath(element, category)) {
                categories.add category
            }
        }
        return categories
    }

    void categorize(List<DataCellElement> elements) {
        for (OutputElement e : elements) {
            // map all categories
            this.addCategoriesToElement(e)

            // create template path
            List<String> wildCards = this.createTemplatePath(e)
            e.setWildCards(wildCards)

            // create wild card path associated with template path or, if already existing, register the category values
            if (!wildCardPaths.containsKey(e.templatePath)) {
                WildCardPath wildCardPath = new WildCardPath(e.templatePath, wildCards)
                wildCardPaths[e.templatePath] = wildCardPath
            }
            WildCardPath wildCardPath = wildCardPaths[e.templatePath]
            for (String category : wildCards) {
                wildCardPath.addWildCardValue(category, e.getCategoryValue(category))
            }
            e.wildCardPath = wildCardPath
        }
    }
}
