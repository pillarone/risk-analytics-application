package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy


interface ISimulationProvider {

    Simulation getSimulation()

    ICollectorOutputStrategy getOutputStrategy()
}
