package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationUIItem extends ModellingUIItem {

    public SimulationUIItem(RiskAnalyticsMainModel model, Model simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    String createTitle() {
        if (((Simulation) item).start != null) return item.name
        return UIUtils.getText(SimulationUIItem.class, "simulation")
    }

    ULCContainer createDetailView() {
        SimulationConfigurationView view = new SimulationConfigurationView(viewModel)
        return view.content
    }

    SimulationConfigurationModel getViewModel() {
        SimulationConfigurationModel model = new SimulationConfigurationModel(this.model.class, mainModel)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        mainModel.addModelChangedListener(model.settingsPaneModel)
        mainModel.addModelChangedListener(model.actionsPaneModel)
        mainModel.registerModel(this, model)
        return model
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof SimulationUIItem)) {
            return false
        }
        return createTitle().equals((obj as SimulationUIItem).createTitle())
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(createTitle())
        return hcb.toHashCode()
    }
}
