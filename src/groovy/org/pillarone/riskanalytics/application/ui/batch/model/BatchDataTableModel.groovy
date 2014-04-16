package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.table.AbstractTableModel
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchRunAction
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Simulation

import static org.pillarone.riskanalytics.core.simulation.SimulationState.ERROR
import static org.pillarone.riskanalytics.core.simulation.SimulationState.FINISHED

public class BatchDataTableModel extends AbstractTableModel implements BatchTableListener {
    List<String> columnHeaders
    List<List<String>> tableValues
    Batch batch
    Simulation selectedRun
    PollingBatchRunAction pollingBatchRunAction
    List<IRiskAnalyticsModelListener> riskAnalyticsModelListeners
    PollingSupport pollingSupport

    private static final String SIMULATION_STATUS_COLUMN = UIUtils.getText(this.class, "SimulationStatus")

    public BatchDataTableModel(Batch batch) {
        this.tableValues = new ArrayList<List<String>>()
        columnHeaders = []
        this.batch = batch
        riskAnalyticsModelListeners = []
    }

    public void init() {
        pollingSupport = Holders.grailsApplication.mainContext.getBean('pollingSupport2000', PollingSupport)
        initTableHeader()
        batch.simulations.eachWithIndex { Simulation simulation, int index ->
            tableValues << toList(simulation)
        }
        startPolling()
    }

    protected List toList(Simulation simulation) {
        List list = new ArrayList()
        list << simulation.name
        list << simulation.modelClass.simpleName
        list << simulation.parameterization?.name + " v" + simulation.parameterization.versionNumber.toString()
        list << simulation.template?.name + " v" + simulation.template.versionNumber.toString()
        list << simulation.periodCount + "/" + simulation.numberOfIterations
        list << simulation.randomSeed
        list << UIUtils.getText(this.class, simulation.strategy.toString())
        list << UIUtils.getText(this.class, simulation.simulationState.toString())
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

    public int getRowIndex(Simulation simulation) {
        batch.simulations.indexOf(simulation)
    }

    public Simulation getSimulationAt(int rowIndex) {
        batch.simulations[rowIndex]
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

    public void fireTableRowsUpdated(Simulation simulation) {
        def index = getRowIndex(simulation)
        if (index != -1) {
            this.tableValues[index][getColumnIndex(SIMULATION_STATUS_COLUMN)] = UIUtils.getText(this.class, simulation.simulationState.toString())
            fireTableRowsUpdated index, index
        }
    }

    public void fireRowAdded(Simulation simulation) {
        //TODO
        if (simulation.batchId == batch.id) {
            batch.simulations << simulation
            tableValues << toList(simulation)
            int index = tableValues.size() - 1
            fireTableRowsInserted(index, index);
            startPollingTimer()
        }
    }

    public void fireRowDeleted(Simulation run) {
        int rowIndex = getRowIndex(run)
        if (rowIndex >= 0) fireRowDeleted(rowIndex)
    }

    public void fireRowDeleted(int rowIndex) {
        tableValues.remove(rowIndex)
        batch.simulations.remove(rowIndex)
        fireTableRowsDeleted rowIndex, rowIndex
    }

    public void firePriorityChanged(int rowIndex, int step) {
        int to = (rowIndex + step >= 0 && rowIndex + step < rowCount) ? rowIndex + step : rowIndex
        Collections.swap(tableValues, rowIndex, to)
        Collections.swap(batch.simulations, rowIndex, to)
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
        batch.simulations.every { Simulation simulation ->
            simulation.simulationState == FINISHED || simulation.simulationState == ERROR
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

    void fireRowAdded(Simulation batchRun)

    void fireRowDeleted(int rowIndex)

    void fireRowDeleted(Simulation run)

    void firePriorityChanged(int rowIndex, int step)
}
