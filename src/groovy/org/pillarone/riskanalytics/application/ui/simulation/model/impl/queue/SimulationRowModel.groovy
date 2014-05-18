package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo

class SimulationRowModel extends AbstractTableRowModel<SimulationRuntimeInfo> {
    static final Map<Integer, String> COLUMN_NAME_KEYS = [
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
            0: { SimulationRuntimeInfo info -> info.simulation?.nameAndVersion },
            1: { SimulationRuntimeInfo info -> info.simulation?.batch?.name },
            2: { SimulationRuntimeInfo info -> info.parameterization?.nameAndVersion },
            3: { SimulationRuntimeInfo info -> info.resultConfiguration?.nameAndVersion },
            4: { SimulationRuntimeInfo info -> info.iterations?.toString() },
            5: { SimulationRuntimeInfo info -> info.priority?.toString() },
            6: { SimulationRuntimeInfo info -> info.configuredAt?.toString() },
            7: { SimulationRuntimeInfo info -> info.offeredBy?.username },
            8: { SimulationRuntimeInfo info -> info.simulationState?.toString() }
    ]

    static final int COLUMN_COUNT = 9

    SimulationRowModel(int row, AbstractTableModel tableModel, SimulationRuntimeInfo info) {
        super(row, tableModel, info, COLUMN_COUNT)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}
