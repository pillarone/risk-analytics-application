package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchRunAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.output.SimulationRun

public class BatchDataTableModel extends AbstractTableModel implements BatchTableListener {
    List<String> columnHeaders
    List<List<String>> tableValues
    List<BatchRunSimulationRun> batchRunSimulationRuns = []
    BatchRun batchRun
    SimulationRun selectedRun
    ULCPollingTimer pollingBatchRunTimer
    PollingBatchRunAction pollingBatchRunAction

    private final String SIMULATION_STATUS_COLUMN = UIUtils.getText(this.class, "SimulationStatus")

    public BatchDataTableModel(BatchRun batchRun) {
        this.tableValues = new ArrayList<List<String>>()
        columnHeaders = []
        this.batchRun = batchRun
        init()
        pollingBatchRunAction = new PollingBatchRunAction(this)
        startPollingTimer pollingBatchRunAction
    }


    private void init() {
        BatchRun.withTransaction {
            batchRun = BatchRun.findByName(batchRun.name)
            initTableHeader()
            batchRunSimulationRuns = BatchRunSimulationRun.findAllByBatchRun(batchRun, [sort: "priority", order: "asc"])
            batchRunSimulationRuns.eachWithIndex {BatchRunSimulationRun brSr, int index ->
                tableValues << toList(brSr)
            }
        }
    }

    private List toList(BatchRunSimulationRun brSr) {
        List list = new ArrayList()
        list << brSr.simulationRun.name
        int ptIndex = brSr.simulationRun.model.lastIndexOf(".")
        list << ((ptIndex > 0) ? brSr.simulationRun.model.substring(ptIndex + 1) : brSr.simulationRun.model)
        list << brSr.simulationRun.parameterization.name + " v" + brSr.simulationRun.parameterization.itemVersion
        list << brSr.simulationRun.resultConfiguration.name + " v" + brSr.simulationRun.resultConfiguration.itemVersion
        list << brSr.simulationRun.periodCount + "/" + brSr.simulationRun.iterations
        list << brSr.strategy.toString()
        list << brSr.simulationState.toString()
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
        int index = 0;
        for (BatchRunSimulationRun batchRunSimulationRun: batchRunSimulationRuns) {
            if (batchRunSimulationRun.simulationRun.name == simulationRun.name)
                return index
            index++
        }
        return -1
    }

    public SimulationRun getSimulationRunAt(int rowIndex) {
        return batchRunSimulationRuns.get(rowIndex).simulationRun
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private void initTableHeader() {
        this.columnHeaders = [UIUtils.getText(this.class, "Name"), UIUtils.getText(this.class, "Model")]
        this.columnHeaders << UIUtils.getText(this.class, "Parameterization")
        this.columnHeaders << UIUtils.getText(this.class, "SimulationTemplate")
        this.columnHeaders << UIUtils.getText(this.class, "PeriodCountIterations")
        this.columnHeaders << UIUtils.getText(this.class, "Strategy")
        this.columnHeaders << SIMULATION_STATUS_COLUMN
    }

    public void fireTableRowsUpdated(BatchRunSimulationRun batchRunSimulationRun) {
        List<Integer> rows = []
        batchRunSimulationRuns.eachWithIndex {BatchRunSimulationRun brsr, int index ->
            if (batchRunSimulationRun.simulationRun.name == brsr.simulationRun.name) {
                rows << index
                this.tableValues.get(index).set(getColumnIndex(SIMULATION_STATUS_COLUMN), batchRunSimulationRun.simulationState.toString())
            }
        }
        rows.each {
            fireTableRowsUpdated it, it
        }
    }

    public void fireRowAdded() {
        BatchRunSimulationRun batchRunSimulationRun = BatchRunService.getService().addedBatchRunSimulationRun
        batchRunSimulationRuns << batchRunSimulationRun
        tableValues << toList(batchRunSimulationRun)
        int index = tableValues.size() - 1
        fireTableRowsInserted(index, index);
    }

    public void fireRowDeleted(int rowIndex) {
        tableValues.remove(rowIndex)
        batchRunSimulationRuns.remove(rowIndex)
        fireTableRowsDeleted rowIndex, rowIndex
    }

    public void firePriorityChanged(int rowIndex, int step) {
        int to = (rowIndex + step >= 0 && rowIndex + step < getRowCount()) ? rowIndex + step : rowIndex
        Collections.swap(tableValues, rowIndex, to)
        Collections.swap(batchRunSimulationRuns, rowIndex, to)
        fireTableRowsUpdated Math.min(rowIndex, to), Math.max(rowIndex, to)
    }

    private void startPollingTimer(PollingBatchRunAction pollingBatchRunAction) {
        pollingBatchRunTimer = new ULCPollingTimer(2000, pollingBatchRunAction)
        pollingBatchRunTimer.repeats = true
        pollingBatchRunTimer.start()
    }

    private int getColumnIndex(String columnName) {
        return columnHeaders.indexOf(columnName)
    }
}

interface BatchTableListener {

    void fireRowAdded()

    void fireRowDeleted(int rowIndex)

    void firePriorityChanged(int rowIndex, int step)
}
