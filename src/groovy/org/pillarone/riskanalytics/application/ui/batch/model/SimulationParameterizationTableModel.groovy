package org.pillarone.riskanalytics.application.ui.batch.model

import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SortableTableModel
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class SimulationParameterizationTableModel extends SortableTableModel<BatchRowInfo> {

    private static final Map<Integer, String> COLUMN_NAMES = [
            0: 'Name',
            1: 'Model',
            2: 'Template',
            3: 'Period/Iterations',
            4: 'Random Seed',
            5: 'Simulation State'

    ] as Map<Integer, String>

    private static final Map<Integer, Closure> COLUMN_VALUE_FACTORIES = [
            0: { BatchRowInfo batchRowInfo -> batchRowInfo.name },
            1: { BatchRowInfo batchRowInfo -> batchRowInfo.modelName },
            2: { BatchRowInfo batchRowInfo -> batchRowInfo.templateName },
            3: { BatchRowInfo batchRowInfo -> batchRowInfo.periodIterationAsString },
            4: { BatchRowInfo batchRowInfo -> batchRowInfo.randomSeed },
            5: { BatchRowInfo batchRowInfo -> batchRowInfo.simulationStateAsString }
    ] as Map<Integer, Closure>

    private final Batch batch

    SimulationParameterizationTableModel(Batch batch, String simulationProfileName) {
        super([])
        backedList = createBatchRowInfos(batch, simulationProfileName)
        this.batch = batch
    }

    @Override
    String getColumnName(int column) {
        COLUMN_NAMES[column]
    }

    @Override
    int getColumnCount() {
        COLUMN_NAMES.size()
    }

    @Override
    Object getValueAt(int row, int column) {
        COLUMN_VALUE_FACTORIES[column].call(backedList[row])
    }

    private List<BatchRowInfo> createBatchRowInfos(Batch batch, String simulationProfileName) {
        Map<Class, SimulationProfile> byModelClass = batchRunService.getSimulationProfilesGroupedByModelClass(simulationProfileName)
        batch.parameterizations.collect {
            new BatchRowInfo(parameterization: it, simulationProfile: byModelClass[it.modelClass])
        }
    }

    BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    void simulationProfileNameChanged(String name) {
        backedList = createBatchRowInfos(batch, name)
        fireTableDataChanged()
    }

    List<BatchRowInfo> getBatchRowInfos() {
        backedList
    }
}
