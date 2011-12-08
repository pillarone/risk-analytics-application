package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryColumnMapping
import com.ulcjava.base.application.table.AbstractTableModel

/**
 */
class OutputElementTableModel extends AbstractTableModel {
    CategoryColumnMapping categories
    List<OutputElement> allElements = []

    OutputElementTableModel(List<OutputElement> elements, CategoryColumnMapping categoryColumnMapping) {
        categories = categoryColumnMapping
        if (elements) {
            allElements.addAll elements
        }
    }

    int getRowCount() {
        return allElements.size()
    }

    int getColumnCount() {
        return categories.getSize()
    }

    OutputElement getRowElement(int rowIndex) {
        return allElements.get(rowIndex) // TODO : check whether this rowIndex is the model row index
    }

    String getColumnName(int col) {
        return categories.get(col)
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
            categories.addCategory(category)
        }
    }

    void addRows(List<OutputElement> elements) {
        elements.each { el ->
            addRow(el)
        }
    }

    void addCategory(String category) {
        categories.addCategory(category)
    }

    boolean isCellEditable(int i, int i1) {
        return false
    }

    void setValueAt(Object o, int i, int i1) {

    }
}
