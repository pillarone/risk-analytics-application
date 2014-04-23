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

    private static final Map<Integer, String> COLUMN_NAME_KEYS = [
            0: 'simulation',
            1: 'batch',
            2: 'p14n',
            3: 'template',
            4: 'iterations',
            5: 'priority',
            6: 'configuredAt',
            7: 'configuredBy',
            8: 'simulationState'
    ] as Map<Integer, String>

    private static final Map<Integer, Closure<String>> COLUMN_VALUE_FACTORIES = [
            0: { SimulationRuntimeInfo info -> info.simulation.nameAndVersion },
            1: { SimulationRuntimeInfo info -> info.simulation.batch?.name },
            2: { SimulationRuntimeInfo info -> info.parameterization.nameAndVersion },
            3: { SimulationRuntimeInfo info -> info.resultConfiguration.nameAndVersion },
            4: { SimulationRuntimeInfo info -> info.iterations?.toString() },
            5: { SimulationRuntimeInfo info -> info.priority?.toString() },
            6: { SimulationRuntimeInfo info -> info.configuredAt.toString() },
            7: { SimulationRuntimeInfo info -> info.offeredBy?.username },
            8: { SimulationRuntimeInfo info -> info.simulationState.toString() }
    ]

    protected final List<SimulationRuntimeInfo> infos = []

    @Override
    int getRowCount() {
        infos.size()
    }

    @Override
    int getColumnCount() {
        COLUMN_NAME_KEYS.size()
    }

    SimulationRuntimeInfo getInfoAt(int index) {
        infos[index]
    }

    @Override
    String getColumnName(int column) {
        getText(COLUMN_NAME_KEYS[column])
    }

    private String getText(String key) {
        UIUtils.getText(SimulationQueueTableModel, key)
    }

    @Override
    String getValueAt(int row, int column) {
        COLUMN_VALUE_FACTORIES[column].call(infos[row])
    }

    void setQueueItems(List<SimulationRuntimeInfo> queueItems) {
        this.infos.clear()
        this.infos.addAll(queueItems)
        fireTableDataChanged()
    }

    void itemAdded(SimulationRuntimeInfo item) {
        infos.add(item)
        infos.sort()
        fireTableDataChanged()
    }

    void itemRemoved(SimulationRuntimeInfo info) {
        def index = infos.indexOf(info)
        if (index != -1) {
            infos.remove(index)
            fireTableRowsDeleted(index, index)
        }
    }

    void itemChanged(SimulationRuntimeInfo info) {
        def index = infos.indexOf(info)
        if (index != -1) {
            //only update cell which can change:
            fireTableCellUpdated(index, 7)
        }
    }
}
