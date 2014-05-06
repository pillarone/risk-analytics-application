package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.view.ResultConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultConfigurationUIItem extends ModellingUiItemWithModel {

    ResultConfigurationUIItem(ResultConfiguration resultConfiguration) {
        super(resultConfiguration)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    IDetailView createDetailView() {
        return new ResultConfigurationView(viewModel, riskAnalyticsMainModel)
    }

    private ResultConfigurationViewModel getViewModel() {
        ResultConfigurationViewModel model = new ResultConfigurationViewModel(this.model, (ResultConfiguration) item, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = riskAnalyticsMainModel
        return model
    }

    @Override
    List<Simulation> getSimulations() {
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
    boolean isEditable() {
        return item.editable
    }

    @Override
    @CompileStatic
    ResultConfiguration getItem() {
        super.getItem() as ResultConfiguration
    }
}
