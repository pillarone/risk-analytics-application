package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.ITableModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.core.simulation.item.Simulation

import javax.annotation.PostConstruct

class SimulationQueueViewModel {
    SimulationQueueService simulationQueueService
    ITableModel queueTableModel

    @PostConstruct
    void initialize() {
        queueTableModel = new SimulationQueueTableModel(queueItems)
    }

    private List<IQueueItem> getQueueItems() {
        simulationQueueService.sortedQueueEntries.collect {
            new IQueueItem() {
                @Override
                Simulation getSimulation() {
                    it.simulationConfiguration.simulation
                }

                @Override
                String getBatchRun() {
                    ""
                }

                @Override
                String getP14n() {
                    simulation.parameterization.toString()
                }

                @Override
                String getResultConfiguration() {
                    simulation.template?.toString()
                }

                @Override
                Integer getIterations() {
                    simulation.numberOfIterations
                }

                @Override
                Integer getPriority() {
                    it.priority
                }

                @Override
                String getAddedBy() {
                    ""
                }

                @Override
                String getConfiguredAt() {
                    ""
                }
            }

        }
    }
}
