package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.model.IContentModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.CalculationConfigurationView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationUIItem extends SimulationUIItem {


    public CalculationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, Simulation simulation) {
        super(mainModel, simulationModel, simulation)
    }

    public CalculationUIItem(RiskAnalyticsMainModel mainModel, AbstractTableTreeModel tableTreeModel,
                             Model simulationModel, Simulation simulation) {
        super(mainModel, tableTreeModel, simulationModel, simulation)
    }

    String createTitle() {
        return null  
    }

    ULCContainer createDetailView() {
        CalculationConfigurationView view = new CalculationConfigurationView(getViewModel())
        return view.content
    }

    IContentModel getViewModel() {
        CalculationConfigurationModel model = new CalculationConfigurationModel(model.class, mainModel)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        model.actionsPaneModel.addSimulationListener(tableTreeModel)
        mainModel.addModelChangedListener(model.settingsPaneModel)
        mainModel.registerModel(this, model)

        return model
    }

}
