package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemFactory {

    public
    static ModellingUIItem createItem(ModellingItem modellingItem, Model model) {
        switch (modellingItem.class) {
            case Parameterization.class: return new ParameterizationUIItem(model, (Parameterization) modellingItem)
            case Resource.class: return new ResourceUIItem((Resource) modellingItem)
            case ResultConfiguration.class: return new ResultConfigurationUIItem(model, (ResultConfiguration) modellingItem)
            case Batch.class: return new BatchUIItem((Batch) modellingItem)
            default: throw new IllegalArgumentException("${modellingItem.class.simpleName} not yet supported")
        }
    }

    public
    static SimulationResultUIItem createItem(Simulation simulation, Model model) {
        if (DeterministicModel.class.isAssignableFrom(simulation.modelClass)) {
            return new DeterministicResultUIItem((DeterministicModel) model, simulation)
        } else if (StochasticModel.class.isAssignableFrom(simulation.modelClass)) {
            return new StochasticResultUIItem((StochasticModel) model, simulation)
        } else {
            throw new IllegalArgumentException("modelClass ${simulation.modelClass} not supported")
        }
    }

}
