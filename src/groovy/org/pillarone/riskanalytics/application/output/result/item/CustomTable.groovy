package org.pillarone.riskanalytics.application.output.result.item

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.output.CustomTableDAO
import org.pillarone.riskanalytics.application.output.CustomTableEntry
import org.pillarone.riskanalytics.application.output.CustomTableEntryPair
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMappingRegistry
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory


class CustomTable extends ModellingItem {

    public static final String TEXT_KEY = "text"
    Class modelClass

    List<List> tableData

    Parameterization parameterization


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
        customTableDAO.person = UserManagement.currentUser
        customTableDAO.parameterization = ParameterizationDAO.find(parameterization.name, parameterization.modelClass.name, parameterization.versionNumber.toString())
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
                    entry.text = element
                } else if (element instanceof DataCellElement) {
                    for (Map.Entry<String, String> category in element.categoryMap) {
                        if (category.value.startsWith("=")) {
                            entry.addToPairs(new CustomTableEntryPair(entryKey: category.key, entryValue: category.value))
                        }
                    }
                    entry.path = PathMapping.findByPathName(element.path)
                    entry.field = FieldMapping.findByFieldName(element.field)
                    entry.collector = CollectorMapping.findByCollectorName(element.collector)
                    entry.periodIndex = element.period
                    entry.keyFigure = element.statistics.toString()
                    entry.keyFigureParameter = element.parameter
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
        parameterization = ModellingItemFactory.getParameterization(customTableDAO.parameterization)
        List<DataCellElement> elements = []

        tableData = []
        int maxRow = customTableDAO.entries*.row.max()
        for (int i = 0; i <= maxRow; i++) {
            final List<CustomTableEntry> entries = customTableDAO.entries.findAll { it.row == i }.sort { it.col }
            List rowData = []
            for (CustomTableEntry entry in entries) {
                if (entry.text != null) {
                    rowData << entry.text
                } else {
                    DataCellElement element = new DataCellElement(period: entry.periodIndex, path: entry.path.pathName, field: entry.field.fieldName, collector: entry.collector.collectorName)
                    for (CustomTableEntryPair pair in entry.pairs) {
                        element.addCategoryValue(pair.entryKey, pair.entryValue)
                    }
                    element.addCategoryValue(OutputElement.PATH, element.path)
                    element.addCategoryValue(OutputElement.FIELD, element.field)
                    element.addCategoryValue(OutputElement.COLLECTOR, element.collector)
                    element.addCategoryValue(OutputElement.PERIOD, element.period.toString())
                    element.addCategoryValue(OutputElement.STATISTICS, entry.keyFigure)
                    element.addCategoryValue(OutputElement.STATISTICS_PARAMETER, entry.keyFigureParameter.toString())
                    elements << element
                    rowData << element
                }

            }
            tableData << rowData
        }

        CategoryMapping categoryMapping = CategoryMappingRegistry.getCategoryMapping(new SimulationRun(model: customTableDAO.modelClassName))
        if (categoryMapping) {
            categoryMapping.categorize(elements)
        }

    }

    @Override
    protected loadFromDB() {
        return CustomTableDAO.findByNameAndModelClassName(name, modelClass.name)
    }
}
