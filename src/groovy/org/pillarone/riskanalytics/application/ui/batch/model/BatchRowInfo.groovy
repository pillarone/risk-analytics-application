package org.pillarone.riskanalytics.application.ui.batch.model

import com.google.common.base.Preconditions
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

import static org.pillarone.riskanalytics.core.simulation.SimulationState.FINISHED

class BatchRowInfo {
    private final Parameterization parameterization
    private SimulationProfile simulationProfile
    private Simulation simulation

    BatchRowInfo(Parameterization parameterization) {
        this.parameterization = Preconditions.checkNotNull(parameterization)
    }

    String getName() {
        if (simulation) {
            return simulation.nameAndVersion
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
        if (simulation) {
            return "${simulation.periodCount}/${simulation.numberOfIterations}"
        }
        if (simulationProfile) {
            return "${parameterization.periodCount}/${simulationProfile?.numberOfIterations}"
        }
        return ''
    }

    String getRandomSeed() {
        if (simulation) {
            return simulation.randomSeed
        }
        simulationProfile?.randomSeed ?: ''
    }

    String getSimulationStateAsString() {
        simulation ? simulation.simulationState : ''
    }

    Parameterization getParameterization() {
        return parameterization
    }

    boolean isFinished() {
        simulation?.simulationState == FINISHED
    }

    void setSimulationProfile(SimulationProfile simulationProfile) {
        this.simulationProfile = simulationProfile
    }

    void setSimulationRuntimeInfo(SimulationRuntimeInfo simulationRuntimeInfo) {
        simulation = simulationRuntimeInfo.simulation
    }

    void setSimulation(Simulation simulation) {
        this.simulation = simulation
    }

    boolean isValid() {
        simulationProfile || simulation
    }

    Simulation getSimulation() {
        return simulation
    }

    ResultConfiguration getTemplate() {
        if (simulation) {
            return simulation.template
        }
        if (simulationProfile) {
            return simulationProfile.template
        }
        return null
    }
}
