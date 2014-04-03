package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.ISimulationStateListener
import org.pillarone.riskanalytics.core.simulation.SimulationState


class SimulationStateEventSupport {

    private Set<ISimulationStateListener> listeners = Collections.synchronizedSet([] as Set)

    void addSimulationStateListener(ISimulationStateListener listener) {
        listeners.add(listener)
    }

    void removeSimulationStateListener(ISimulationStateListener listener) {
        listeners.add(listener)
    }

    void notifySimulationStateChanged(SimulationState simulationState) {
        listeners.each { it.simulationStateChanged(simulationState) }
    }
}
