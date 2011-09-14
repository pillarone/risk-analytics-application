package org.pillarone.riskanalytics.application.ui.chart.model

import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.ChartInsetWriter
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.SeriesColor
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils
import java.awt.Color

abstract class ChartViewModel {
    protected List series = []
    List<String> seriesNames = []
    Map showLine = [:]
    boolean showPeriodLabels = true
    Map periodLabels = [:]

    String title
    SimulationRun simulationRun
    List listeners
    List nodes
    boolean dataExportMode
    boolean fireEvents = true

    ChartProperties chartProperties

    protected static float chartLineThickness = 2.0

    ChartInsetWriter chartInsetWriter


    boolean onlyStochasticSeries = true
    def notStochasticSeries = [:]

    public drawLegend = true

    SeriesColor seriesColor;

    public ChartViewModel() {}

    public ChartViewModel(String title, SimulationRun simulationRun, List nodes, double insetHeight) {


        this.title = title
        this.simulationRun = simulationRun
        this.nodes = nodes
        this.listeners = []
        this.chartInsetWriter = new ChartInsetWriter(insetHeight)
        nodes.size().times {int i ->
            showLine[[i, 0]] = true
        }
        loadData()
        seriesColor = new SeriesColor(periodCount)
    }

    abstract JFreeChart getChart()

    Map getDataTable() {
        throw new UnsupportedOperationException("Data Export not supported by this chart type")
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
            data << ["Chart Type:", title]
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

    public int getPeriodCount() {
        simulationRun.periodCount
    }

    public String getPeriodLabel(int periodIndex, boolean periodLabelForPath = false) {
        String label
        if (showPeriodLabels || periodLabelForPath) {
            if (periodLabels[periodIndex]) {
                label = periodLabels[periodIndex]
            } else {
                ResultViewUtils.initPeriodLabels(SimulationRun.get(simulationRun.id), periodLabels)
                label = periodLabels[periodIndex]
            }

        } else {
            label = "P" + periodIndex
        }
        return label
    }

    public void setSeriesVisibility(int seriesIndex, int period, boolean value) {
        showLine[[seriesIndex, period]] = value
        fireModelChanged()
    }

    public boolean getSeriesVisibility(int seriesIndex, int period) {
        if (showLine[[seriesIndex, period]]) {
            return showLine[[seriesIndex, period]]
        }
        return false
    }

    public boolean allFromOnePeriodSelected(int period) {
        int nrOfSeries = seriesNames.size()
        boolean allSelected = true
        nrOfSeries.times {int seriesIndex ->
            allSelected = allSelected && showLine[[seriesIndex, period]]
        }
        return allSelected
    }

    public boolean allFromOneKeyFigureSelected(int seriesIndex) {
        boolean allSelected = true
        periodCount.times {int period ->
            allSelected = allSelected && showLine[[seriesIndex, period]]
        }
        return allSelected
    }

    public void selectAllFromPeriod(int period, boolean select = true, boolean notify = true) {
        int nrOfSeries = seriesNames.size()
        nrOfSeries.times {int seriesIndex ->
            showLine[[seriesIndex, period]] = select && notStochasticSeries[seriesNames[seriesIndex], period] == null
        }
        if (notify) {
            fireModelChanged()
        }
    }

    public void selectAllFromKeyFigure(int seriesIndex, boolean select = true) {
        periodCount.times {int period ->
            showLine[[seriesIndex, period]] = select
        }
        fireModelChanged()
    }

    public void selectAll(boolean select) {
        periodCount.times {int periodIndex ->
            selectAllFromPeriod(periodIndex, select, false)
        }
        fireModelChanged()
    }

    public boolean allSelected() {
        if (showLine.size() >= periodCount * seriesNames.size()) {
            boolean allSelected = true
            seriesNames.size().times {int seriesIndex ->
                periodCount.times {int period ->
                    if (notStochasticSeries[seriesNames[seriesIndex], period] == null)
                        allSelected = allSelected && showLine[[seriesIndex, period]]
                }

            }
            return allSelected
        } else {
            return false
        }
    }

    public void deselectAllFromPeriod(int period) {
        selectAllFromPeriod(period, false)
    }

    public void deselectAllFromKeyFigure(int seriesIndex) {
        selectAllFromKeyFigure(seriesIndex, false)
    }

    protected void loadData() {
        nodes.each {ResultTableTreeNode node ->
            List periods = []
            periodCount.times {int periodIndex ->
                onlyStochasticSeries = onlyStochasticSeries && ResultAccessor.hasDifferentValues(simulationRun, periodIndex, node.path, node.collector, node.field)
                periods << ResultAccessor.getValues(simulationRun, periodIndex, node.path, node.collector, node.field)
            }
            series << periods
            seriesNames << node.getShortDisplayPath(nodes)
        }
    }

    void addListener(IModelChangedListener listener) {
        listeners.add(listener)
    }

    void removeListener(IModelChangedListener listener) {
        listeners.remove(listener)
    }

    void fireModelChanged() {
        if (fireEvents) {
            listeners.each {it.modelChanged()}
        }
    }


    protected void writeInsetContent(ChartInsetWriter writer) {}

    public boolean isChangeColorEnabled() {
        return true
    }

    protected void setBackground(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(color);
        chart.getPlot().setBackgroundPaint(color);
        chart.getPlot().setRangeGridlinePaint(Color.lightGray);
    }
}

class ChartProperties {

    String title
    String xAxisTitle
    String yAxisTitle
    boolean showLegend

}