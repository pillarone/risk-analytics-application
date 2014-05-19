package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.CalculationConfigurationView
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationSettingsUIItem extends ModellingUiItemWithModel<SimulationConfigurationView> {

    SimulationSettingsUIItem(Simulation simulation) {
        super(simulation)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    String createTitle() {
        return UIUtils.getText(SimulationSettingsUIItem.class, stochasticModel ? "simulation" : "calculation")
    }

    SimulationConfigurationView createDetailView() {
        return stochasticModel ? new SimulationConfigurationView(simulationConfigurationModel) : new CalculationConfigurationView(calculationConfigurationModel)
    }

    private boolean isStochasticModel() {
        model instanceof StochasticModel
    }

    private CalculationConfigurationModel getCalculationConfigurationModel() {
        CalculationConfigurationModel model = new CalculationConfigurationModel(model.class)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        return model
    }

    private SimulationConfigurationModel getSimulationConfigurationModel() {
        SimulationConfigurationModel model = new SimulationConfigurationModel(this.model.class)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
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
        return model.name.equals((obj as SimulationSettingsUIItem).model.name)
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(model.name)
        return hcb.toHashCode()
    }

    @Override
    @CompileStatic
    Simulation getItem() {
        super.getItem() as Simulation
    }
}
