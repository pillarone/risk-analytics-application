package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.view.ResultConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultConfigurationUIItem extends ModellingUIItem {

    ResultConfigurationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, ResultConfiguration resultConfiguration) {
        super(mainModel, simulationModel, resultConfiguration)
    }

    ULCContainer createDetailView() {
        ResultConfigurationView view = new ResultConfigurationView(viewModel, mainModel)
        mainModel.addModelItemChangedListener(view)
        return view.content
    }

    ResultConfigurationViewModel getViewModel() {
        ResultConfigurationViewModel model = new ResultConfigurationViewModel(this.model, (ResultConfiguration) item, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = mainModel
        mainModel.registerModel(this, model)
        return model
    }

    @Override
    List<SimulationRun> getSimulations() {
        return item.simulations
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("resulttemplate-active.png")
    }

    @Override
    boolean isVersionable() {
        return true
    }

    @Override
    boolean isChangeable() {
        return true
    }

    @Override
    boolean isEditable() {
        return item.editable
    }

    @Override
    @CompileStatic
    ResultConfiguration getItem() {
        super.getItem() as ResultConfiguration
    }
}
