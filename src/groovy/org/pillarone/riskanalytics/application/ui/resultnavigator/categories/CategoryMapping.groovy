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
        if (element.templatePath==null) {
            element.templatePath=new String(element.getPath())
        }
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

    void categorize(List<OutputElement> elements) {
        for (OutputElement e : elements) {
            // map all categories (including resolvers based on paths and fields)
            this.addCategoriesToElement(e)

            // create template path, get the wild cards for the path
            List<String> pathWildCards = this.createTemplatePath(e)
            e.setWildCards(pathWildCards)

            // create wild card path associated with template path or, if already existing, register the category values
            if (!wildCardPaths.containsKey(e.templatePath)) {
                WildCardPath wildCardPath = new WildCardPath()
                wildCardPath.setWildCardPath(e.templatePath, pathWildCards)
                wildCardPaths[e.templatePath] = wildCardPath
            }
            WildCardPath wildCardPath = wildCardPaths[e.templatePath]
            for (String category : pathWildCards) {
                wildCardPath.addPathWildCardValue(category, e.getCategoryValue(category))
            }

            // fields and associated synonym resolver
            String synonymToField = null
            Iterator<String> it = matcherMap.keySet().iterator()
            while (synonymToField==null && it.hasNext()) {
                String key = it.next()
                ICategoryResolver resolver = matcherMap[key]
                if (resolver instanceof SynonymToCategory && ((SynonymToCategory)resolver).category.equals(OutputElement.FIELD)) {
                    synonymToField = key
                }
            }
            if (synonymToField==null) {
                synonymToField = OutputElement.FIELD
            }
            wildCardPath.addWildCardValue(synonymToField, e.getCategoryValue(OutputElement.FIELD))

            e.wildCardPath = wildCardPath
        }
    }
}
