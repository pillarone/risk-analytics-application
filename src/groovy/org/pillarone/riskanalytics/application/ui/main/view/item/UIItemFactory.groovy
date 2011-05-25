package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemFactory {

    Log LOG = LogFactory.getLog(UIItemFactory)

    public static ModellingUIItem createItem(ModellingItem modellingItem, Model model, RiskAnalyticsMainModel mainModel, AbstractTableTreeModel tableTreeModel) {
        switch (modellingItem.class) {
            case Parameterization.class: return new ParameterizationUIItem(mainModel, tableTreeModel, model, (Parameterization) modellingItem)
            case ResultConfiguration.class: return new ResultConfigurationUIItem(mainModel, tableTreeModel, model, (ResultConfiguration) modellingItem)
            default: throw new IllegalArgumentException("modellingItem ${modellingItem.name} not supported")
        }
    }

    public static ModellingUIItem createItem(Simulation simulation, Model model, RiskAnalyticsMainModel mainModel, AbstractTableTreeModel tableTreeModel) {
        if (DeterministicModel.class.isAssignableFrom(simulation.modelClass))
            return new DeterministicResultUIItem(mainModel, tableTreeModel, (DeterministicModel) model, simulation)
        else if (StochasticModel.class.isAssignableFrom(simulation.modelClass))
            return new StochasticResultUIItem(mainModel, tableTreeModel, (StochasticModel) model, simulation)
        else
            throw new IllegalArgumentException("modelClass ${simulation.modelClass} not supported")
    }


}
