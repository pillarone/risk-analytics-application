package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import org.pillarone.riskanalytics.core.simulation.SimulationState


interface ISimulationStateListener {
    void simulationStateChanged(SimulationState simulationState)
}
