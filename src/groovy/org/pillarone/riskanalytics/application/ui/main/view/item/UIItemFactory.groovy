package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemFactory {

    public
    static ModellingUIItem createItem(ModellingItem modellingItem, Model model, RiskAnalyticsMainModel mainModel) {
        switch (modellingItem.class) {
            case Parameterization.class: return new ParameterizationUIItem(mainModel, model, (Parameterization) modellingItem)
            case Resource.class: return new ResourceUIItem(mainModel, model, (Resource) modellingItem)
            case ResultConfiguration.class: return new ResultConfigurationUIItem(mainModel, model, (ResultConfiguration) modellingItem)
            case Batch.class: return new BatchUIItem(mainModel, (Batch) modellingItem)
            default: throw new IllegalArgumentException("${modellingItem.class.simpleName} not yet supported")
        }
    }

    public
    static SimulationResultUIItem createItem(Simulation simulation, Model model, RiskAnalyticsMainModel mainModel) {
        if (DeterministicModel.class.isAssignableFrom(simulation.modelClass)) {
            return new DeterministicResultUIItem(mainModel, (DeterministicModel) model, simulation)
        } else if (StochasticModel.class.isAssignableFrom(simulation.modelClass)) {
            return new StochasticResultUIItem(mainModel, (StochasticModel) model, simulation)
        } else {
            throw new IllegalArgumentException("modelClass ${simulation.modelClass} not supported")
        }
    }

}
