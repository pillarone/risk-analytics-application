package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueTableModel extends AbstractTableModel {

    protected final List<SimulationRowModel> columnModels = []

    @Override
    int getRowCount() {
        columnModels.size()
    }

    @Override
    int getColumnCount() {
        SimulationRowModel.COLUMN_COUNT
    }

    SimulationRuntimeInfo getInfoAt(int index) {
        columnModels[index].object
    }

    @Override
    String getColumnName(int column) {
        getText(SimulationRowModel.COLUMN_NAME_KEYS[column])
    }

    private String getText(String key) {
        UIUtils.getText(SimulationQueueTableModel, key)
    }

    @Override
    String getValueAt(int row, int column) {
        columnModels[row].getValueAt(column)
    }

    void setInfos(List<SimulationRuntimeInfo> infos) {
        this.columnModels.clear()
        infos.eachWithIndex { SimulationRuntimeInfo info, int row ->
            this.columnModels << new SimulationRowModel(row, this, info)
        }
        fireTableDataChanged()
    }

    void itemAdded(SimulationRuntimeInfo item) {
        columnModels.add(0, new SimulationRowModel(columnModels.size(), this, item))
        sortColumnModels()
        fireTableDataChanged()
    }

    protected void sortColumnModels() {
        columnModels.sort { it.object }
        assignRowsToColumnModels()
    }

    void itemRemoved(SimulationRuntimeInfo info) {
        SimulationRowModel columnModel = columnModels.find { SimulationRowModel model -> model.object == info }
        if (columnModel) {
            int index = columnModels.indexOf(columnModel)
            columnModels.remove(columnModel)
            assignRowsToColumnModels()
            fireTableRowsDeleted(index, index)
        }
    }

    protected void assignRowsToColumnModels() {
        columnModels.eachWithIndex { SimulationRowModel columnModel, int row ->
            columnModel.row = row
        }
    }

    void itemChanged(SimulationRuntimeInfo info) {
        SimulationRowModel columnModel = columnModels.find { SimulationRowModel model -> model.object == info }
        if (columnModel) {
            columnModel.setObject(info)
        }
    }
}

