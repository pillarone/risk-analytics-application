package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.event.TableModelEvent
import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRunSimulationRun

/**
 * @author fouad jaada
 */

public class BatchDataTableModel implements ITableModel {
    List<String> columnHeaders
    List tableValues
    List listeners = []
    BatchRun batchRun
    SimulationRun selectedRun

    public BatchDataTableModel(BatchRun batchRun) {
        this.tableValues = new ArrayList<List<String>>()
        columnHeaders = []
        this.batchRun = batchRun
        init()
    }


    private void init() {
        BatchRun.withTransaction {
            batchRun = BatchRun.findByName(batchRun.name)
            this.columnHeaders = [UIUtils.getText(this.class, "Name"), UIUtils.getText(this.class, "Model"), UIUtils.getText(this.class, "Parameterization"), UIUtils.getText(this.class, "SimulationTemplate"), UIUtils.getText(this.class, "PeriodCountIterations"), UIUtils.getText(this.class, "Strategy")]
            batchRun.batchRunService.getSimulationRuns(batchRun).eachWithIndex {BatchRunSimulationRun brSr, int index ->
                List list = new ArrayList()
                list << brSr.simulationRun.name
                int ptIndex = brSr.simulationRun.model.lastIndexOf(".")
                list << ((ptIndex > 0) ? brSr.simulationRun.model.substring(ptIndex + 1) : brSr.simulationRun.model)
                list << brSr.simulationRun.parameterization.name + " v" + brSr.simulationRun.parameterization.itemVersion
                list << brSr.simulationRun.resultConfiguration.name + " v" + brSr.simulationRun.resultConfiguration.itemVersion
                list << brSr.simulationRun.periodCount + "/" + brSr.simulationRun.iterations
                list << brSr.strategy.toString()
                tableValues << list
            }
        }
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

    public void addTableModelListener(ITableModelListener listener) {
        listeners << listener
    }

    public void removeTableModelListener(ITableModelListener listener) {
        listeners.remove(listener)
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    protected fireModelChanged() {
        this.tableValues = new ArrayList<List<String>>()
        init()
        TableModelEvent event = new TableModelEvent(this)
        listeners.each {ITableModelListener listener -> listener.tableChanged(event)}
    }


}
