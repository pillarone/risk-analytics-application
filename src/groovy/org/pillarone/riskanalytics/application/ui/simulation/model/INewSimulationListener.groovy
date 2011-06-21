package org.pillarone.riskanalytics.application.ui.simulation.model

import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
/**
 * update of p14n and template selection by creating a new simulation in UI
 */
interface INewSimulationListener {

    void newSimulation(Simulation simulation)
}
