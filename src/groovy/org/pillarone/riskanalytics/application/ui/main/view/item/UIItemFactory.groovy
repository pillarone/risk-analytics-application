package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.*

class UIItemFactory {

    static ModellingUIItem createItem(ModellingItem modellingItem) {
        switch (modellingItem.class) {
            case Parameterization.class: return new ParameterizationUIItem((Parameterization) modellingItem)
            case Resource.class: return new ResourceUIItem((Resource) modellingItem)
            case ResultConfiguration.class: return new ResultConfigurationUIItem((ResultConfiguration) modellingItem)
            case Batch.class: return new BatchUIItem((Batch) modellingItem)
            default: throw new IllegalArgumentException("${modellingItem.class.simpleName} not yet supported")
        }
    }

    static SimulationResultUIItem createItem(Simulation simulation) {
        if (DeterministicModel.class.isAssignableFrom(simulation.modelClass)) {
            return new DeterministicResultUIItem(simulation)
        } else if (StochasticModel.class.isAssignableFrom(simulation.modelClass)) {
            return new StochasticResultUIItem(simulation)
        } else {
            throw new IllegalArgumentException("modelClass ${simulation.modelClass} not supported")
        }
    }

}
