package org.pillarone.riskanalytics.application.ui.simulation.model

import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class CalculationConfigurationModel extends AbstractConfigurationModel {

    Integer periodCount

    public CalculationConfigurationModel(P1RATModel mainModel, Class modelClass, Parameterization parameterization, ResultConfiguration template) {
        super(mainModel, modelClass, parameterization, template)
    }

    /**
     * Creates a Simulation object specific for DeterministicModels.
     * This means that the period count is read from the model and the iteration count is always 1.
     * All other settings are read from the model as well.
     */
    public Simulation getSimulation() {
        if (this.simulationName == null || this.simulationName.trim().length() == 0) {
            setSimulationName(new SimpleDateFormat("yyyy.MM.dd kk:mm:ss").format(new Date()))
        }

        Simulation run = new Simulation(simulationName)
        run.modelClass = selectedModel
        run.comment = comment
        Parameterization parameterization = availableParameterizationVersionsForModel.selectedObject as Parameterization
        parameterization.load()
        run.parameterization = parameterization
        ResultConfiguration configuration = availableResultConfigurationVersionsForModel.selectedObject as ResultConfiguration
        configuration.load()
        run.template = configuration
        run.numberOfIterations = 1
        run.periodCount = periodCount
        if ((selectedModel.newInstance() as Model).requiresStartDate()) {
            run.beginOfFirstPeriod = beginOfFirstPeriod
        }
        run.randomSeed = randomSeed
        run.modelVersionNumber = ModellingItemFactory.getNewestModelItem(selectedModel.simpleName).versionNumber // ???

        return run
    }

    public boolean isSimulationStartEnabled() {
        return periodCount != null && periodCount != 0

    }

    public void setPeriodCount(Integer count) {
        periodCount = count
        notifySimulationConfigurationChanged()
    }

}
