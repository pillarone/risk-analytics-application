package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.item.Simulation

interface ISimulationOrderChangedListener {

    void orderChanged(Simulation simulation, int oldIndex, int newIndex)
}
