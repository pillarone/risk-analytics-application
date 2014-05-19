package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.table.AbstractTableModel
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.AbstractTableRowModel

@CompileStatic
class BatchRowInfoRowModel extends AbstractTableRowModel<BatchRowInfo> {

    final static int SIZE = 7

    static final Map<Integer, String> COLUMN_NAMES = [
            0: 'Name',
            1: 'Model',
            2: 'Template',
            3: 'Period/Iterations',
            4: 'Random Seed',
            5: 'Simulation State',
            6: 'Time'

    ] as Map<Integer, String>

    private static final Map<Integer, Closure> COLUMN_VALUE_FACTORIES = [
            0: { BatchRowInfo batchRowInfo -> batchRowInfo.name },
            1: { BatchRowInfo batchRowInfo -> batchRowInfo.modelName },
            2: { BatchRowInfo batchRowInfo -> batchRowInfo.templateName },
            3: { BatchRowInfo batchRowInfo -> batchRowInfo.periodIterationAsString },
            4: { BatchRowInfo batchRowInfo -> batchRowInfo.randomSeed },
            5: { BatchRowInfo batchRowInfo -> batchRowInfo.simulationStateAsString },
            6: { BatchRowInfo batchRowInfo -> batchRowInfo.durationAsString }
    ] as Map<Integer, Closure>

    BatchRowInfoRowModel(int row, AbstractTableModel tableModel, BatchRowInfo object, int columnCount) {
        super(row, tableModel, object, columnCount)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}
