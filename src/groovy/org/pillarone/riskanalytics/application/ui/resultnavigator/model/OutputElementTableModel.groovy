package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryChangeListener

/**
 */
class OutputElementTableModel extends AbstractTableModel implements ICategoryChangeListener {

    /**
     * Each element of this list corresponds to one row in the table - if not hidden by an active filter
     */
    List<OutputElement> allElements = []

    /**
     * Category mapping that is typically defined for a given model and that is used to generate the
     * category information map of categories and associated values (keywords). This category information
     * ends up in different columns of the table.
     */
    CategoryMapping categoryMapping

    /**
     * Template mode is set to active once the template filter has been selected.
     * This filter is rather a different view than a filter. Depending on whether the
     * template mode is active different values are found in the different cells.
     * For the 'path' column the templatePath and for the category columns
     * the different values that can be inserted as wild cards are shown
     * if the template mode is active.
     */
    boolean isTemplateMode

    /**
     * Provides data for the selection of the period and the statistics key figure
     */
    KeyfigureSelectionModel keyFigureSelectionModel

    private Map<Integer, String> colToCategoryMap = new HashMap<Integer, String>()
    private Map<String, Integer> categoryToColMap = new HashMap<String, Integer>()
    private List<String> categories = new LinkedList<String>();

    OutputElementTableModel(List<OutputElement> elements, CategoryMapping categoryMapping, KeyfigureSelectionModel keyFigureSelectionModel) {
        this.categoryMapping = categoryMapping
        addCategory(OutputElement.PATH)
        addCategory(OutputElement.FIELD)
        for (String category : categoryMapping.categories) {
            addCategory(category)
        }
        if (elements) {
            allElements.addAll elements
        }
        this.keyFigureSelectionModel = keyFigureSelectionModel
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
            if (isTemplateMode) {
                if (columnName==OutputElement.PATH) {
                    return element.templatePath
                } else {
                    WildCardPath wcp = element.getWildCardPath()
                    List<String> list = wcp.getWildCardValues(columnName)
                    return list ? list.toListString() : ""
                }
            } else {
                return element.getCategoryValue(columnName)
            }
        } else {
            return null
        }
    }

    boolean isCellEditable(int i, int i1) {
        return false
    }

    void setValueAt(Object o, int i, int i1) {

    }

    void categoryAdded(String category) {
        addCategory(category)
    }

    void categoryRemoved(String category) {
        removeCategory(category)
    }

    private void addCategory(String category) {
        int index = categories.size()
        categories.add category
        colToCategoryMap.put(index, category)
        categoryToColMap.put(category, index)
    }

    private void removeCategory(String category) {
        if (categories.contains(category)) {
            categories.remove category
            int index = categoryToColMap.remove(category)
            colToCategoryMap.remove(index)
        }
    }

    /*private void addRow(OutputElement element) {
        allElements.add element
        element.getCategoryMap().each { category, memberValue ->
            if (!categories.contains(category)) {
                addCategory category
            }
        }
    }

    private void addRows(List<OutputElement> elements) {
        elements.each { el ->
            addRow(el)
        }
    }*/

}
