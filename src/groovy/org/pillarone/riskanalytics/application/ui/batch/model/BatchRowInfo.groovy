package org.pillarone.riskanalytics.application.ui.batch.model

import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class BatchRowInfo {
    Parameterization parameterization
    SimulationProfile simulationProfile
    SimulationRuntimeInfo simulationRuntimeInfo

    String getName() {
        if (simulationRuntimeInfo) {
            return simulationRuntimeInfo.simulation.nameAndVersion
        }
        parameterization.name
    }

    String getModelName() {
        parameterization.modelClass.simpleName
    }

    Class getModelClass() {
        parameterization.modelClass
    }

    String getTemplateName() {
        simulationProfile?.template?.nameAndVersion ?: ''
    }

    String getPeriodIterationAsString() {
        "${parameterization.periodCount}/${simulationProfile?.numberOfIterations}" ?: ''
    }

    String getRandomSeed() {
        simulationProfile?.randomSeed ?: ''
    }

    String getSimulationStateAsString() {
        simulationRuntimeInfo ? simulationRuntimeInfo.simulationState : ''
    }

    boolean isValid() {
        simulationProfile || simulationRuntimeInfo
    }
}
