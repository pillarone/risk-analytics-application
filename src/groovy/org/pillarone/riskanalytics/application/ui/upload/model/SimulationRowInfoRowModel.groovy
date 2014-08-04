package org.pillarone.riskanalytics.application.ui.upload.model

import com.ulcjava.base.application.table.AbstractTableModel
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.AbstractTableRowModel

@CompileStatic
class SimulationRowInfoRowModel extends AbstractTableRowModel<SimulationRowInfo> {

    final static int SIZE = 6

    static final Map<Integer, String> COLUMN_NAMES = [
            0: 'Name',
            1: 'Model',
            2: 'Template',
            3: 'Iterations',
            4: 'Random Seed',
            5: 'Time'

    ] as Map<Integer, String>

    private static final Map<Integer, Closure> COLUMN_VALUE_FACTORIES = [
            0: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.name },
            1: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.modelName },
            2: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.templateName },
            3: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.iterationAsString },
            4: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.randomSeed },
            5: { SimulationRowInfo simulationRowInfo -> simulationRowInfo.durationAsString }
    ] as Map<Integer, Closure>

    SimulationRowInfoRowModel(int row, AbstractTableModel tableModel, SimulationRowInfo object, int columnCount) {
        super(row, tableModel, object, columnCount)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}
