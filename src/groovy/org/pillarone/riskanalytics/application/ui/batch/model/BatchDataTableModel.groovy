package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.table.AbstractTableModel
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchRunAction
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun

import static org.pillarone.riskanalytics.core.simulation.SimulationState.ERROR
import static org.pillarone.riskanalytics.core.simulation.SimulationState.FINISHED

public class BatchDataTableModel extends AbstractTableModel implements BatchTableListener {
    List<String> columnHeaders
    List<List<String>> tableValues
    BatchRun batchRun
    SimulationRun selectedRun
    PollingBatchRunAction pollingBatchRunAction
    List<IRiskAnalyticsModelListener> riskAnalyticsModelListeners
    PollingSupport pollingSupport

    private static final String SIMULATION_STATUS_COLUMN = UIUtils.getText(this.class, "SimulationStatus")

    public BatchDataTableModel(BatchRun batchRun) {
        this.tableValues = new ArrayList<List<String>>()
        columnHeaders = []
        this.batchRun = batchRun
        riskAnalyticsModelListeners = []
    }

    public void init() {
        pollingSupport = Holders.grailsApplication.mainContext.getBean('pollingSupport2000', PollingSupport)
        initTableHeader()
        BatchRun.withTransaction {
            batchRun = BatchRun.findByName(batchRun.name)
            batchRun.simulationRuns.eachWithIndex { SimulationRun brSr, int index ->
                tableValues << toList(brSr)
            }
        }
        startPolling()
    }

    protected List toList(SimulationRun simulationRun) {
        List list = new ArrayList()
        list << simulationRun.name
        int ptIndex = simulationRun.model.lastIndexOf(".")
        list << ((ptIndex > 0) ? simulationRun.model.substring(ptIndex + 1) : simulationRun.model)
        list << simulationRun?.parameterization?.name + " v" + simulationRun?.parameterization?.itemVersion
        list << simulationRun?.resultConfiguration?.name + " v" + simulationRun?.resultConfiguration?.itemVersion
        list << simulationRun.periodCount + "/" + simulationRun.iterations
        list << simulationRun.randomSeed
        list << UIUtils.getText(this.class, simulationRun.strategy.toString())
        list << UIUtils.getText(this.class, simulationRun.simulationState.toString())
        return list
    }


    public int getRowCount() {
        return tableValues.size()
    }

    public int getColumnCount() {
        if (tableValues[0]) {
            return tableValues[0].size()
        } else {
            return 0
        }
    }

    public String getColumnName(int column) {
        columnHeaders[column]
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return Integer
        } else {
            return Number
        }
    }

    public Object getValueAt(int row, int column) {
        tableValues[row][column]
    }

    public void setValueAt(Object value, int row, int column) {
        tableValues[row][column] = value
    }

    public int getRowIndex(SimulationRun simulationRun) {
        batchRun.simulationRuns.indexOf(simulationRun)
    }

    public SimulationRun getSimulationRunAt(int rowIndex) {
        batchRun.simulationRuns[rowIndex]
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private void initTableHeader() {
        this.columnHeaders = [UIUtils.getText(this.class, "Name"), UIUtils.getText(this.class, "Model")]
        this.columnHeaders << UIUtils.getText(this.class, "Parameterization")
        this.columnHeaders << UIUtils.getText(this.class, "SimulationTemplate")
        this.columnHeaders << UIUtils.getText(this.class, "PeriodCountIterations")
        this.columnHeaders << UIUtils.getText(this.class, "RandomSeed")
        this.columnHeaders << UIUtils.getText(this.class, "Strategy")
        this.columnHeaders << SIMULATION_STATUS_COLUMN
    }

    public void fireTableRowsUpdated(SimulationRun simulationRun) {
        def index = getRowIndex(simulationRun)
        if (index != -1) {
            this.tableValues[index][getColumnIndex(SIMULATION_STATUS_COLUMN)] = UIUtils.getText(this.class, simulationRun.simulationState.toString())
            fireTableRowsUpdated index, index
        }
    }

    public void fireRowAdded(SimulationRun simulationRun) {
        if (simulationRun.batchRun?.id == batchRun.id) {
            batchRun.simulationRuns << simulationRun
            tableValues << toList(simulationRun)
            int index = tableValues.size() - 1
            fireTableRowsInserted(index, index);
            startPollingTimer()
        }
    }

    public void fireRowDeleted(SimulationRun run) {
        int rowIndex = getRowIndex(run)
        if (rowIndex >= 0) fireRowDeleted(rowIndex)
    }

    public void fireRowDeleted(int rowIndex) {
        tableValues.remove(rowIndex)
        batchRun.simulationRuns.remove(rowIndex)
        fireTableRowsDeleted rowIndex, rowIndex
    }

    public void firePriorityChanged(int rowIndex, int step) {
        int to = (rowIndex + step >= 0 && rowIndex + step < rowCount) ? rowIndex + step : rowIndex
        Collections.swap(tableValues, rowIndex, to)
        Collections.swap(batchRun.simulationRuns, rowIndex, to)
        fireTableRowsUpdated Math.min(rowIndex, to), Math.max(rowIndex, to)
    }

    private void startPollingTimer() {
        if (!allExecuted) {
            pollingSupport.addActionListener(pollingBatchRunAction)
        }
    }

    public void stopPollingTimer() {
        if (allExecuted) {
            pollingSupport.removeActionListener(pollingBatchRunAction)
        }
    }

    public void openDetailView(Model model, Object item) {
        riskAnalyticsModelListeners.each { IRiskAnalyticsModelListener riskAnalyticsModelListener ->
            riskAnalyticsModelListener.openDetailView model, item
        }

    }

    private boolean isAllExecuted() {
        batchRun.simulationRuns.every { SimulationRun simulationRun ->
            simulationRun.simulationState == FINISHED || simulationRun.simulationState == ERROR
        }
    }

    private int getColumnIndex(String columnName) {
        return columnHeaders.indexOf(columnName)
    }

    public void addRiskAnalyticsModelListener(IRiskAnalyticsModelListener riskAnalyticsModelListener) {
        riskAnalyticsModelListeners << riskAnalyticsModelListener
    }

    private void startPolling() {
        pollingBatchRunAction = new PollingBatchRunAction(this)
        startPollingTimer()
    }
}

interface BatchTableListener {

    void fireRowAdded(SimulationRun batchRun)

    void fireRowDeleted(int rowIndex)

    void fireRowDeleted(SimulationRun run)

    void firePriorityChanged(int rowIndex, int step)
}
