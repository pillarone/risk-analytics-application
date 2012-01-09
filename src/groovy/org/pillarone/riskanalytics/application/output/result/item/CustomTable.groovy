package org.pillarone.riskanalytics.application.output.result.item

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.output.CustomTableDAO
import org.pillarone.riskanalytics.application.output.CustomTableEntry
import org.pillarone.riskanalytics.application.output.CustomTableEntryPair


class CustomTable extends ModellingItem {

    public static final String TEXT_KEY = "text"
    Class modelClass

    List<List> tableData

    CustomTable(String name, Class modelClass) {
        super(name)
        this.modelClass = modelClass
    }

    @Override
    protected createDao() {
        return new CustomTableDAO(name: name)
    }

    @Override
    def getDaoClass() {
        return CustomTableDAO
    }

    @Override
    protected void mapToDao(Object dao) {
        CustomTableDAO customTableDAO = dao as CustomTableDAO
        customTableDAO.modelClassName = modelClass.name
        if (customTableDAO.entries != null) {
            for (CustomTableEntry entry in new ArrayList<CustomTableEntry>(customTableDAO.entries)) {
                customTableDAO.removeFromEntries(entry)
                entry.delete()
            }
        }

        int i = 0
        for (List row in tableData) {
            int j = 0
            for (def element in row) {
                CustomTableEntry entry = new CustomTableEntry(row: i, col: j)
                if (element instanceof String) {
                    entry.addToPairs(new CustomTableEntryPair(entryKey: TEXT_KEY, entryValue: element))
                } else if (element instanceof OutputElement) {
                    for (Map.Entry<String, String> category in element.categoryMap) {
                        entry.addToPairs(new CustomTableEntryPair(entryKey: category.key, entryValue: category.value))
                    }
                }
                customTableDAO.addToEntries(entry)
                j++
            }
            i++
        }
    }

    @Override
    protected void mapFromDao(Object dao, boolean completeLoad) {
        CustomTableDAO customTableDAO = dao as CustomTableDAO

        tableData = []
        int maxRow = customTableDAO.entries*.row.max()
        for (int i = 0; i <= maxRow; i++) {
            final List<CustomTableEntry> entries = customTableDAO.entries.findAll { it.row == i }.sort { it.col }
            List rowData = []
            for (CustomTableEntry entry in entries) {
                if (entry.pairs.size() == 1 && entry.pairs.toList()[0].entryKey == TEXT_KEY) {
                    rowData << entry.pairs.toList()[0].entryValue
                } else {
                    OutputElement element = new OutputElement()
                    for (CustomTableEntryPair pair in entry.pairs) {
                        element.addCategoryValue(pair.entryKey, pair.entryValue)
                    }

                    rowData << element
                }

            }
            tableData << rowData
        }

    }

    @Override
    protected loadFromDB() {
        return CustomTableDAO.findByNameAndModelClassName(name, modelClass.name)
    }
}
