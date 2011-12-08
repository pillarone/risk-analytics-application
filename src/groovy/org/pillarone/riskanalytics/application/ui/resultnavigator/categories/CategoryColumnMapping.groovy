package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
public class CategoryColumnMapping {
    private Map<Integer, String> colToCategoryMap = new HashMap<Integer, String>()
    private Map<String, Integer> categoryToColMap = new HashMap<String, Integer>()
    private List<String> categories = new LinkedList<String>();

    CategoryColumnMapping() {
        addCategory(OutputElement.PATH)
    }

    public void put(Integer col, String category) {
        colToCategoryMap.put(col, category)
        categoryToColMap.put(category, col)
        if (!categories.contains(category)) {
            categories.add(category)
        }
    }

    public String removeByKey(Integer col) {
        String removedValue = colToCategoryMap.remove(key)
        categoryToColMap.remove(removedValue)
        return removedValue
    }

    public String removeByValue(String category) {
        Integer removedCol = categoryToColMap.remove(category)
        colToCategoryMap.remove(removedCol)
        return removedCol
    }

    public boolean containsKey(Integer col) {
        return colToCategoryMap.containsKey(col)
    }

    public boolean containsValue(String category) {
        return colToCategoryMap.containsValue(category)
    }

    public Integer getKey(String category) {
        return categoryToColMap.get(category)
    }

    public String get(Integer col) {
        return colToCategoryMap.get(col)
    }

    public int getSize() {
        return colToCategoryMap.size()
    }

    public List<String> getCategories() {
        return categories
    }

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add category
            this.put(this.getSize(), category)
        }
    }
}