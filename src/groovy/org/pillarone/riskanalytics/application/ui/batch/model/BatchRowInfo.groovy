package org.pillarone.riskanalytics.application.ui.batch.model

import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class BatchRowInfo {
    Parameterization parameterization
    SimulationProfile simulationProfile
    SimulationState simulationState

    String getName() {
        parameterization.name
    }

    String getModelName() {
        parameterization.modelClass.simpleName
    }

    String getTemplateName() {
        simulationProfile?.template?.nameAndVersion ?: ''
    }

    String getPeriodIterationAsString() {
        simulationProfile?.numberOfIterations ?: ''
    }

    String getRandomSeed() {
        simulationProfile?.randomSeed ?: ''
    }

    String getSimulationStateAsString() {
        simulationState ?: ''
    }

    boolean isValid() {
        simulationProfile || simulationState
    }
}
