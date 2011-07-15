package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaComparator
import org.pillarone.riskanalytics.application.ui.chart.model.QueryPaneModel
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils

class ResultIterationDataViewModel extends QueryPaneModel {
    int periodCount
    ResultIterationDataTableModel resultTableModel
    ResultView resultView
    Boolean isSingleValue

    public ResultIterationDataViewModel(SimulationRun simulationRun, List nodes, boolean autoQueryOnCreate, boolean enablePeriodComboBox = true, boolean showPeriodLabels = true, ResultView resultView) {
        super(simulationRun, nodes, false, enablePeriodComboBox, showPeriodLabels)
        resultTableModel = new ResultIterationDataTableModel()
        if (autoQueryOnCreate) {
            query()
        }
        this.@resultView = resultView
    }

    public ResultIterationDataViewModel(SimulationRun simulationRun, List nodes, boolean autoQueryOnCreate, boolean enablePeriodComboBox = true, boolean showPeriodLabels = true) {
        super(simulationRun, nodes, false, enablePeriodComboBox, showPeriodLabels)
        resultTableModel = new ResultIterationDataTableModel()
        if (autoQueryOnCreate) {
            query()
        }
    }

    public List getRawData() {
        List lines = []
        List rawData = createResultList()
        rawData.each {List rawDataLine ->
            Map excelLine = [:]
            rawDataLine.eachWithIndex {it, index ->
                excelLine[columnHeader[index]] = it
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
        super.query()
        List lastQueryResults = orderByFirstKeyFigure(createResultList())
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

    public void setOrderByPath(boolean value) {
        this.@orderByPath = value
        resultTableModel.tableValues = orderByFirstKeyFigure(createResultList())
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
        SimulationRun.withTransaction {status ->
            simulationRun = SimulationRun.get(simulationRun.id)
            Parameterization parameterization = ModellingItemFactory.getParameterization(simulationRun?.parameterization)
            Class modelClass = parameterization.modelClass
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
            return data
        }
    }

    void adjust(int adjustment) {
        resultTableModel.numberDataType.maxFractionDigits = resultTableModel.numberDataType.maxFractionDigits + adjustment
        resultTableModel.numberDataType.minFractionDigits = resultTableModel.numberDataType.minFractionDigits + adjustment
        resultTableModel.fireModelChanged()
        fireModelChanged()
    }

    boolean isSingle() {
        if (isSingleValue == null) {
            isSingleValue = nodes.any {ResultTableTreeNode node -> node.collector == SingleValueCollectingModeStrategy.IDENTIFIER }
        }
        return isSingleValue
    }


}

