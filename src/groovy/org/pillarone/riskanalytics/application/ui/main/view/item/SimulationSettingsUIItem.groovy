package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationSettingsUIItem extends ModellingUiItemWithModel {

    SimulationSettingsUIItem(Model model, Simulation simulation) {
        super(model, simulation)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    String createTitle() {
        if (((Simulation) item).start != null) return item.name
        return UIUtils.getText(SimulationSettingsUIItem.class, "simulation")
    }

    ULCContainer createDetailView() {
        SimulationConfigurationView view = new SimulationConfigurationView(viewModel)
        return view.content
    }

    SimulationConfigurationModel getViewModel() {
        SimulationConfigurationModel model = new SimulationConfigurationModel(this.model.class, riskAnalyticsMainModel)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        riskAnalyticsMainModel.registerModel(this, model)
        return model
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof SimulationSettingsUIItem)) {
            return false
        }
        return createTitle().equals((obj as SimulationSettingsUIItem).createTitle())
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(createTitle())
        return hcb.toHashCode()
    }

    @Override
    @CompileStatic
    Simulation getItem() {
        super.getItem() as Simulation
    }
}