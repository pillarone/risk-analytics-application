package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaComparator
import org.pillarone.riskanalytics.application.ui.chart.model.QueryPaneModel
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.AggregatedWithSingleAvailableCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.components.ComponentUtils

class ResultIterationDataViewModel extends QueryPaneModel {
    int periodCount
    ResultIterationDataTableModel resultTableModel
    ResultView resultView
    Boolean isSingleValue

    private boolean initialQuery

    private List temporaryResults

    public ResultIterationDataViewModel(SimulationRun simulationRun, List nodes, boolean autoQueryOnCreate, boolean enablePeriodComboBox, boolean showPeriodLabels, ResultView resultView) {
        super(simulationRun, nodes, false, enablePeriodComboBox, showPeriodLabels)
        resultTableModel = new ResultIterationDataTableModel()
        criterias.clear() //PMO-2226
        if (autoQueryOnCreate) {
            initialQuery = true
            query()
            initialQuery = false
        }
        this.@resultView = resultView
    }

    public List getRawData() {
        List lines = []
        List rawData = temporaryResults
        if(rawData == null) { //criteria has been changed since last query
            query()
            rawData = temporaryResults
        }

        List headers = getColumnHeader()

        for (List rawDataLine in rawData) {
            Map excelLine = [:]
            int index = 0
            for (value in rawDataLine) {
                excelLine[headers[index]] = value
                index++
            }
            lines << excelLine
        }
        return lines
    }

    public String getCounterString() {
        int found = results.size()
        java.text.NumberFormat numberFormat = LocaleResources.getNumberFormat()
        "Found ${numberFormat.format(found)} out of ${numberFormat.format(simulationRun.iterations)} Iterations"
    }



    public List getColumnHeader() {
        List columnHeader = ["Iteration"]
        if (orderByPath) {
            shortPaths.each {String path ->
                simulationRun.periodCount.times {int periodIndex ->
                    columnHeader << path + " " + getPeriodLabel(periodIndex)
                }
            }
        } else {
            simulationRun.periodCount.times {int periodIndex ->
                shortPaths.each {
                    columnHeader << it + " " + getPeriodLabel(periodIndex)
                }
            }
        }
        return columnHeader
    }

    public void query() {
        if (!initialQuery) {
            super.query()
        } else {
            results = new HashSet()

            if (criterias.empty) {
                Math.min(1000, simulationRun.iterations).times {
                    results << it + 1
                }
                initialQuery = false
            }
        }
        temporaryResults = createResultList()
        List lastQueryResults = orderByFirstKeyFigure(temporaryResults)
        resultTableModel.tableValues = lastQueryResults
        resultTableModel.columnHeaders = columnHeader
        if (resultTableModel.tableValues.size() == 0) {
            resultTableModel.tableValues = [[]]
            resultTableModel.columnHeaders.each {def d ->
                resultTableModel.tableValues[0] << ""
            }
        }
        resultTableModel.fireModelChanged()
        fireModelChanged()
    }

    @Override
    void criteriaChanged() {
        super.criteriaChanged()
        temporaryResults = null
    }

    public void setOrderByPath(boolean value) {
        this.@orderByPath = value
        temporaryResults = createResultList()
        resultTableModel.tableValues = orderByFirstKeyFigure(temporaryResults)
        resultTableModel.columnHeaders = columnHeader
        resultTableModel.fireModelChanged()
        fireModelChanged()
    }

    protected List orderByFirstKeyFigure(List l) {
        if (criterias.empty || criterias[0].empty) {
            return l
        }
        String path = criterias[0][0].selectedPath
        int periodIndex = criterias[0][0].selectedPeriod
        CriteriaComparator comparator = criterias[0][0].selectedComparator
        String periodLabel = periodIndex != null ? getPeriodLabel(periodIndex) : "All periods"
        String columnHeader = shortPaths[paths.findIndexOf { it.equals(path)}] + " " + periodLabel
        int columnHeaderIndex = getColumnHeader().findIndexOf { it.equals(columnHeader)}
        List sortedList = l.sort { it[columnHeaderIndex] }
        if (comparator == CriteriaComparator.LESS_EQUALS || comparator == CriteriaComparator.LESS_THAN) {
            sortedList = sortedList.reverse()
        }
        return sortedList
    }

    public List<List<String>> getSimulationSettings() {
        Simulation simulation = ModellingItemFactory.getSimulation(simulationRun)
        simulation.load()

        List data = []
        data << ["", "", "Version"]
        data << ["Simulation Name:", "$simulation.name"]
        data << ["Comment:", simulation.comment]
        data << ["Model:", "${simulation.modelClass.name}", "${simulation.modelVersionNumber}"]
        data << ["Parameterization:", "$simulation.parameterization.name", "${simulation.parameterization.versionNumber.toString()}"]
        data << ["Template:", "$simulation.template.name", "${simulation.template.versionNumber.toString()}"]
        data << ["Structure:", "$simulation.structure.name", "${simulation.structure.versionNumber.toString()}"]
        data << ["Number of Periods:", simulation.periodCount]
        data << ["Number of Iterations:", simulation.numberOfIterations]
        data << ["Simulation end Date:", DateFormatUtils.formatDetailed(simulation.end)]
        data << ["Random seed", simulation.randomSeed]
        return data
    }

    public List<List<String>> getRuntimeParameters() {
        Simulation simulation = ModellingItemFactory.getSimulation(simulationRun)
        simulation.load()

        List data = []
        for (ParameterHolder runtimeParameter in simulation.runtimeParameters.sort { it.path }) {
            data << [ComponentUtils.getNormalizedName(runtimeParameter.path), runtimeParameter.businessObject.toString()]
        }
        return data
    }

    void adjust(int adjustment) {
        resultTableModel.numberDataType.maxFractionDigits = resultTableModel.numberDataType.maxFractionDigits + adjustment
        resultTableModel.numberDataType.minFractionDigits = resultTableModel.numberDataType.minFractionDigits + adjustment
        resultTableModel.fireModelChanged()
        fireModelChanged()
    }

    boolean isSingle() {
        if (isSingleValue == null) {
            isSingleValue = nodes.any {ResultTableTreeNode node -> node.collector == AggregatedWithSingleAvailableCollectingModeStrategy.IDENTIFIER }
        }
        return isSingleValue
    }


}

