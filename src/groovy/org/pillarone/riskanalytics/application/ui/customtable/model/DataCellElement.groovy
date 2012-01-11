package org.pillarone.riskanalytics.application.ui.customtable.model

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

/**
 *
 * @author ivo.nussbaumer
 */
class DataCellElement extends OutputElement {
    Object value
    int periodIndex

    public DataCellElement () {
    }

    public DataCellElement (OutputElement outputElement) {
        this.run = outputElement.run
        this.path = outputElement.path
        this.templatePath = outputElement.templatePath
        this.field = outputElement.field
        this.collector = outputElement.collector
        this.categoryMap = outputElement.categoryMap.clone()
        this.wildCards = outputElement.wildCards
        this.wildCardPath = outputElement.wildCardPath
    }

    // Copy constructor
    public DataCellElement (DataCellElement dataCellElement) {
        this.run = dataCellElement.run
        this.path = dataCellElement.path
        this.templatePath = dataCellElement.templatePath
        this.field = dataCellElement.field
        this.collector = dataCellElement.collector
        this.categoryMap = dataCellElement.categoryMap.clone()
        this.wildCards = dataCellElement.wildCards
        this.wildCardPath = dataCellElement.wildCardPath

        this.value = dataCellElement.value
        this.periodIndex = dataCellElement.periodIndex
    }


    public void updateValue() {
        try {
            // TODO: include period and statistics
            value = ResultAccessor.getMean (run, periodIndex, path, collector, field)
        } catch (Exception e) {
            value = "#ERROR"
        }
    }


    public boolean updateSpecificPathWithVariables (CustomTableModel customTableModel) {
        Map<String, String> categoryMapCopy = new HashMap<String, String>()

        for (String category : this.categoryMap.keySet()) {
            String value = this.categoryMap[category]
            if (value.startsWith("=")) {
                value = customTableModel.getValueAt(value.substring(1))
            }
            categoryMapCopy.put (category, value)
        }

        String new_path = this.getWildCardPath().getSpecificPath(categoryMapCopy)

        if (this.path.equals(new_path) == false) {
            this.path = new_path
            return true
        }

        return false
    }
}
