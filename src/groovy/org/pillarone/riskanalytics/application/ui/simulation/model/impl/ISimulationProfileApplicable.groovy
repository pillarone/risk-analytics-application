package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

interface ISimulationProfileApplicable {
    SimulationProfile createProfile(String name)

    void applyProfile(SimulationProfile profile)
}
