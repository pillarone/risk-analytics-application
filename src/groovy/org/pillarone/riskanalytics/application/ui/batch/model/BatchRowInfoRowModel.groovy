package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.table.AbstractTableModel
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.AbstractTableRowModel

@CompileStatic
class BatchRowInfoRowModel extends AbstractTableRowModel<BatchRowInfo> {

    final static int SIZE = 7

    static final Map<Integer, String> COLUMN_NAMES = [
            0: 'Name',
            1: 'Version',
            2: 'Model',
            3: 'Template',
            4: 'Period/Iterations',
            5: 'Random Seed',
            6: 'Simulation State'

    ] as Map<Integer, String>

    private static final Map<Integer, Closure> COLUMN_VALUE_FACTORIES = [
            0: { BatchRowInfo batchRowInfo -> batchRowInfo.name },
            1: { BatchRowInfo batchRowInfo -> batchRowInfo.parameterizationVersion },
            2: { BatchRowInfo batchRowInfo -> batchRowInfo.modelName },
            3: { BatchRowInfo batchRowInfo -> batchRowInfo.templateName },
            4: { BatchRowInfo batchRowInfo -> batchRowInfo.periodIterationAsString },
            5: { BatchRowInfo batchRowInfo -> batchRowInfo.randomSeed },
            6: { BatchRowInfo batchRowInfo -> batchRowInfo.simulationStateAsString }
    ] as Map<Integer, Closure>

    BatchRowInfoRowModel(int row, AbstractTableModel tableModel, BatchRowInfo object, int columnCount) {
        super(row, tableModel, object, columnCount)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}
