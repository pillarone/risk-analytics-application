package org.pillarone.riskanalytics.application.ui.simulation.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
interface ISimulationListener {

    void simulationStart(Simulation simulation)

    void simulationEnd(Simulation simulation, Model model)
}
