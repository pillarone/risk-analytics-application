package org.pillarone.riskanalytics.application.ui.upload.model

import com.google.common.base.Preconditions
import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

@CompileStatic
class SimulationRowInfo {
    private SimulationProfile simulationProfile
    private final Simulation simulation
    private String durationAsString

    SimulationRowInfo(Simulation simulation) {
        Preconditions.checkNotNull(simulation)
        this.simulation = simulation
        calculateDurationFromSimulation()
    }

    String getDurationAsString() {
        durationAsString
    }

    String getName() {
        simulation.nameAndVersion
    }

    String getModelName() {
        parameterization.modelClass.simpleName
    }

    Class getModelClass() {
        parameterization.modelClass
    }

    String getTemplateName() {
        simulation.template?.nameAndVersion
    }

    String getIterationAsString() {
        simulation.numberOfIterations
    }

    String getRandomSeed() {
        simulation.randomSeed
    }

    String getSimulationStateAsString() {
        simulation ? simulation.simulationState : ''
    }

    Parameterization getParameterization() {
        return simulation.parameterization
    }

    String getParameterizationVersion() {
        parameterization.versionNumber.toString()
    }

    boolean isValid() {
        simulationProfile || simulation
    }

    Simulation getSimulation() {
        return simulation
    }

    ResultConfiguration getTemplate() {
        simulation.template
    }

    void setSimulationProfile(SimulationProfile simulationProfile) {
        this.simulationProfile = simulationProfile
    }

    private void calculateDurationFromSimulation() {
        DateTime start = simulation.start
        DateTime end = simulation.end
        Preconditions.checkNotNull(start)
        Preconditions.checkNotNull(end)
        Period period = new Period(start, end, PeriodType.minutes())
        durationAsString = "${period.minutes} min"
    }
}
