package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueViewModel {
    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService
    @Autowired
    SimulationRuntimeService simulationRuntimeService
    @Resource
    SimulationQueueTableModel simulationQueueTableModel
    @Autowired
    SimulationQueueService simulationQueueService
    @Resource
    RiskAnalyticsMainModel riskAnalyticsMainModel

    private final ISimulationRuntimeInfoListener infoListener = new MyInfoListener()

    @PostConstruct
    void initialize() {
        ulcSimulationRuntimeService.addSimulationRuntimeInfoListener(infoListener)
        simulationQueueTableModel.queueItems = simulationRuntimeService.queued
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(infoListener)
    }

    void cancelAt(int index) {
        if (index != -1) {
            SimulationRuntimeInfo info = simulationQueueTableModel.getInfoAt(index)
            simulationQueueService.cancel(info.id)
        }
    }

    void openResultAt(int index) {
        if (index != -1) {
            SimulationRuntimeInfo info = simulationQueueTableModel.getInfoAt(index)
            if (info.simulationState == SimulationState.FINISHED) {
                Simulation simulation = info.simulation
                if (!simulation.loaded) {
                    simulation.load()
                }
                riskAnalyticsMainModel.notifyOpenDetailView((Model) simulation.modelClass.newInstance(), simulation)
            }
        }
    }

    SimulationRuntimeInfo getSimulationRuntimeInfoAt(int index) {
        index != -1 ? simulationQueueTableModel.getInfoAt(index) : null
    }

    private class MyInfoListener implements ISimulationRuntimeInfoListener {

        @Override
        void starting(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemChanged(info)

        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemChanged(info)

        }

        @Override
        void removed(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemRemoved(info)

        }

        @Override
        void offered(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemAdded(info)

        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            simulationQueueTableModel.itemChanged(info)

        }
    }
}
