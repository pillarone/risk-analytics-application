package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

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
        Parameterization parameterization = parameterizationVersions.selectedObject
        if (parameterization == null) {
            return false
        }
        return periodCount != null && parameterization.valid
    }


}
