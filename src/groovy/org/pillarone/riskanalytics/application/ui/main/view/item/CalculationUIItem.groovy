package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.CalculationConfigurationView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationUIItem extends SimulationUIItem {


    public CalculationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, Simulation simulation) {
        super(mainModel, simulationModel, simulation)
    }


    String createTitle() {
        return UIUtils.getText(SimulationUIItem.class, "calculation")
    }

    ULCContainer createDetailView() {
        CalculationConfigurationView view = new CalculationConfigurationView(getViewModel())
        return view.content
    }

    Object getViewModel() {
        CalculationConfigurationModel model = new CalculationConfigurationModel(model.class, mainModel)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        model.actionsPaneModel.addSimulationListener(mainModel)
        mainModel.addModelChangedListener(model.settingsPaneModel)
        mainModel.registerModel(this, model)

        return model
    }



}
