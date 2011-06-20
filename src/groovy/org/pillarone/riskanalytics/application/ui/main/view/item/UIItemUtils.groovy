package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemUtils {

    public static boolean deleteDependingResults(RiskAnalyticsMainModel mainModel, Model model, ModellingUIItem modellingUIItem) {
        return deleteDependingResults(mainModel, model, modellingUIItem.item)
    }

    public static boolean deleteDependingResults(RiskAnalyticsMainModel mainModel, Model model, ModellingItem item) {
        List<SimulationRun> simulationRuns = item.getSimulations();
        //check if at least one simulation is running
        for (SimulationRun simulationRun: simulationRuns) {
            if (!simulationRun.endTime) return false
        }
        for (SimulationRun simulationRun: simulationRuns) {
            Simulation simulation = ModellingItemFactory.getSimulation(simulationRun.name, model.modelClass)
            SimulationUIItem simulationUIItem = new SimulationUIItem(mainModel, model, simulation)
            simulationUIItem.remove()
        }
        return true
    }


}
