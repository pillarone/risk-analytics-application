package org.pillarone.riskanalytics.application.ui.simulation.model

import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationConfigurationModel extends AbstractConfigurationModel {

    public SimulationConfigurationModel(P1RATModel mainModel, Class modelClass, Parameterization parameterization, ResultConfiguration template) {
        super(mainModel, modelClass, parameterization, template)
    }

    /**
     * Creates a new Simulation object for the currently opened SimulationConfigurationView.
     * All data is obtained from the user input except for the period count.
     * Only StochasticModels use this view & model.
     * Therefore the period count is obtained from the model instance, because it might differ from
     * the parameterization period count.
     */
    public Simulation getSimulation() {
        if (this.simulationName == null || this.simulationName.trim().length() == 0) {
            setSimulationName(new SimpleDateFormat("yyyy.MM.dd kk:mm:ss").format(new Date()))
        }

        Simulation simulation = new Simulation(simulationName)
        simulation.modelClass = selectedModel
        simulation.comment = comment
        def parameterization = availableParameterizationVersionsForModel.selectedObject as Parameterization
        parameterization.load()
        simulation.parameterization = parameterization
        def configuration = availableResultConfigurationVersionsForModel.selectedObject as ResultConfiguration
        configuration.load()
        simulation.template = configuration
        simulation.numberOfIterations = iterationCount
        StochasticModel modelInstance = (StochasticModel) simulation.modelClass.newInstance()
        simulation.periodCount = parameterization.periodCount
        if (modelInstance.requiresStartDate()) {
            simulation.beginOfFirstPeriod = beginOfFirstPeriod
        }
        if (useUserDefinedSeed) {
            simulation.randomSeed = randomSeed
        } else {
            long millis = System.currentTimeMillis()
            long millisE5 = millis / 1E5
            simulation.randomSeed = millis - millisE5 * 1E5
        }
        simulation.modelVersionNumber = ModellingItemFactory.getNewestModelItem(selectedModel.simpleName).versionNumber // ???

        return simulation
    }

    public boolean isSimulationStartEnabled() {
        !simulationRunning() && iterationCount != null && iterationCount != 0
    }


}

interface ISimulationListener {
    void simulationStart(Simulation simulation)

    void simulationEnd(Simulation simulation, Model model)
}

interface ISimulationConfigurationListener {
    void simulationConfigurationChanged()
}

