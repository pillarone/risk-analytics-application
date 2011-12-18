package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryChangeListener

/**
 */
class OutputElementTableModel extends AbstractTableModel implements ICategoryChangeListener {
    private Map<Integer, String> colToCategoryMap = new HashMap<Integer, String>()
    private Map<String, Integer> categoryToColMap = new HashMap<String, Integer>()
    private List<String> categories = new LinkedList<String>();
    CategoryMapping categoryMapping

    List<OutputElement> allElements = []

    OutputElementTableModel(List<OutputElement> elements, CategoryMapping categoryMapping) {
        this.categoryMapping = categoryMapping
        addCategory(OutputElement.PATH)
        addCategory(OutputElement.FIELD)
        for (String category : categoryMapping.categories) {
            addCategory(category)
        }
        if (elements) {
            allElements.addAll elements
        }
    }

    int getRowCount() {
        return allElements.size()
    }

    OutputElement getRowElement(int rowIndex) {
        return allElements.get(rowIndex) // TODO : check whether this rowIndex is the model row index
    }

    List<String> getCategories() {
        return categories
    }

    int getColumnCount() {
        return categories.size()
    }

    String getColumnName(int col) {
        return colToCategoryMap.get(col)
    }

    int getColumnIndex(String colName) {
        return categoryToColMap.get(colName)
    }

    Class getColumnClass(int col) {
        return String.class
    }
    
    Object getValueAt(int rowIndex, int columnIndex) {
        OutputElement element = allElements.get(rowIndex)
        String columnName = getColumnName(columnIndex)
        if (columnName) {
            return element.getCategoryValue(columnName)
        } else {
            return null
        }
    }

    void addRow(OutputElement element) {
        allElements.add element
        element.getCategoryMap().each { category, memberValue ->
            if (!categories.contains(category)) {
                addCategory category
            }
        }
    }

    void addRows(List<OutputElement> elements) {
        elements.each { el ->
            addRow(el)
        }
    }

    void addCategory(String category) {
        int index = categories.size()
        categories.add category
        colToCategoryMap.put(index, category)
        categoryToColMap.put(category, index)
    }

    void removeCategory(String category) {
        if (categories.contains(category)) {
            categories.remove category
            int index = categoryToColMap.remove(category)
            colToCategoryMap.remove(index)
        }
    }

    void categoryAdded(String category) {
        addCategory(category)
    }

    void categoryRemoved(String category) {
        removeCategory(category)
    }

    boolean isCellEditable(int i, int i1) {
        return false
    }

    void setValueAt(Object o, int i, int i1) {

    }
}
