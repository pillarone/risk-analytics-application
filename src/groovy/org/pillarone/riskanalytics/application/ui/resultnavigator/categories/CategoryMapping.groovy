package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import java.util.Map.Entry
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.output.CollectingModeFactory

/**
 * @author martin.melchior
 */
class CategoryMapping {

    // category - matcher
    Map<String, ICategoryResolver> matcherMap = [:]

    // templatePath - wildCardPath
    Map<String, WildCardPath> wildCardPaths = [:]

    // listeners that should be updated for changes in the mapping
    List<ICategoryChangeListener> categoryChangeListeners = []

    /**
     *  default constructor
     */
    CategoryMapping() {
        
    }

    /**
     * constructor using the output of the MapCategoriesBuilder builder
    */
    CategoryMapping(MappingEntry categories) {
        this()
        for (MappingEntry categoryEntry : categories.value) {
            String categoryName = categoryEntry.name
            if (((ArrayList)categoryEntry.value).size() != 1) {
                println "Invalid category specification found: category name ${categoryName}."
                throw new RuntimeException("Invalid category specification found: category name ${categoryName}.")
            } 
            Object value = ((MappingEntry) ((ArrayList)categoryEntry.value)[0]).value
            if (!(value instanceof ICategoryResolver)) {
                println "Problem in resolving the mapping for category ${categoryName}."
                throw new RuntimeException("Problem in resolving the mapping for category ${categoryName}.")
            }
            addCategory(categoryName, (ICategoryResolver) value)
        }
    }

    /**
     * register listener
     */
    void addCategoryChangeListener(ICategoryChangeListener listener) {
        if (!categoryChangeListeners.contains(listener)) {
            categoryChangeListeners.add listener
        }
    }

    /**
     * remove registered listener
    */
    void removeCategoryChangeListener(ICategoryChangeListener listener) {
        if (categoryChangeListeners.contains(listener)) {
            categoryChangeListeners.remove listener
        }
    }

    /**
     *
     * @return number of categories hold in the category map
     */
    int getNumberOfCategories() {
        return matcherMap.size()
    }

    /**
     * @return a list with the category descriptors (keys in the category map)
     */
    List<String> getCategories() {
        return matcherMap.keySet().asList()
    }

    /**
     * Add a category without setting an associated matcher
     * @param category category descriptor
     */
    void addCategory(String category) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,null)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryAdded(category)
            }
        }
    }

    /**
     * Add a category by passing a category descriptor and an associated matcher of type ICategoryResolver
     * In case a category with the given category descriptor is already set the call of this method has no effect.
     * Use the method CategoryMapping#setCategory(String, ICategoryResolver) in that situation.
     * @param category
     * @param matcher
     */
    void addCategory(String category, ICategoryResolver matcher) {
        if (!matcherMap.containsKey(category)) {
            matcherMap.put(category,matcher)
            for (ICategoryChangeListener listener : categoryChangeListeners) {
                listener.categoryAdded(category)
            }
        }
    }

    /**
     * Set a category by passing a category descriptor and an associated matcher of type ICategoryResolver.
     * Category entries with the same category descriptor are overwritten.
     * @param category
     * @param matcher
     */
    void setCategory(String category, ICategoryResolver matcher) {
        matcherMap.put(category,matcher)
    }

    /**
     * Check whether the given category is already included in the category map.
     * @param category
     * @return
     */
    boolean hasCategory(String category) {
        return matcherMap.containsKey(category)
    }

    /**
     * Removes category entries from the list.
     * @param category
     * @return true if a matcher associated with the given category descriptor has been removed from the map.
     */
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

    /**
     * Removes all categories.
     */
    void removeAllCategories() {
        for (String category : matcherMap.keySet()) {
            removeCategory(category)
        }
    }

    /**
     * @param category
     * @return Return matcher of type ICategoryResolver for the given category descriptor.
     */
    ICategoryResolver getCategoryMatcher(String category) {
        return matcherMap[category]
    }

    /**
     * Adds the categories to the given output element by resolving the category value
     * found by the given ICategoryResolver and adding that together with the category descriptor
     * in the a dedicated map in the OutputElement.
     * @param element
     */
    void addCategoriesToElement(OutputElement element) {
        for (Entry<String, ICategoryResolver> entry in matcherMap.entrySet()) {
            Object value = entry.value?.getResolvedValue(element)
            if (value && !element.categoryMap.keySet().contains(entry.key)) {
                element.addCategoryValue(entry.key,value)
            }
        }
    }

    /**
     * Create a template path for the given output element and attach it to the output element.
     * The method iterates through the categories (descriptor/matcher) found in the map and
     * replaces the parts that identify the category by wild cards.
     * Furthermore, the value found in this wild card section of the path are included in the
     * category map of the OutputElement. This will allow to recover the path from its template
     * thereafter.
     * Note that the method iteratively adjusts the templatePath field in the OutputElement.
     * The method returns the list of categories that could be identified from the path, i.e.
     * so called wild cards.
     * @param element
     * @return
     */
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

    /**
     * Attaches all the information associated with the category mapping to the OutputElement.
     * Iterates through all the output elements, adds the categories to the output element
     * (including the values to be inserted into the templatePath to recover the original path).
     * Furthermore, the method identifies all different wild card paths (that can be discriminated
     * by different templatePath's). These wild card paths are put in a suitable map (using the
     * templatePath as keys).
     * @param elements
     */
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
