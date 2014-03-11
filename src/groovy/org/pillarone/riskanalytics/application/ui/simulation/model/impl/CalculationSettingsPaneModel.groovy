package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationSettingsPaneModel extends SimulationSettingsPaneModel {

    Integer periodCount

    public CalculationSettingsPaneModel(Class modelClass) {
        super(modelClass)
    }

    @Override
    void initConfigParameters(Simulation simulation, int periodCount) {
        simulation.numberOfIterations = 1
        simulation.periodCount = periodCount
        simulation.randomSeed = randomSeed
        simulation.periodCount = this.periodCount
    }

    void setPeriodCount(Integer i) {
        periodCount = i
        notifyConfigurationChanged()
    }

    protected boolean validate() {
        Parameterization parameterization = parameterizationVersions.selectedObject as Parameterization
        if (parameterization == null) {
            return false
        }
        return periodCount != null && parameterization.valid
    }

    @Override
    void applyTemplate(SimulationProfile profile) {
        throw new UnsupportedOperationException("simulation profiles are not supported for stochastic models")
    }

    @Override
    SimulationProfile createTemplate(String name) {
        throw new UnsupportedOperationException("simulation profiles are not supported for stochastic models")
    }
}
