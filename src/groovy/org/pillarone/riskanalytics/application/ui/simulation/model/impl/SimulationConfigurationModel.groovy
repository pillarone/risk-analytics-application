package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.google.common.eventbus.Subscribe
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.search.ModellingItemEvent

class SimulationConfigurationModel {

    SimulationProfilePaneModel simulationProfilePaneModel
    final RiskAnalyticsMainModel mainModel

    SimulationConfigurationModel(Class modelClass, RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        initSubModels(modelClass)
        attachListener()
    }

    protected void attachListener() {
        mainModel.register(this)
    }

    void close() {
        mainModel.unregister(this)
    }

    protected initSubModels(Class modelClass) {
        simulationProfilePaneModel = new SimulationProfilePaneModel(modelClass, mainModel)
    }

    @Subscribe
    void onSimulationSettingsChangedEvent(SimulationSettingsChangedEvent event) {
        if (checkModelClass(event.modelClass)) {
            if (event.parameterization) {
                settingsPaneModel.selectedParameterization = event.parameterization
            }
            if (event.template) {
                settingsPaneModel.selectedResultConfiguration = event.template
            }
        }
    }

    @Subscribe
    void onModellingItemEvent(ModellingItemEvent event) {
        simulationProfilePaneModel.settingsPaneModel.onEvent(event)
    }

    private boolean checkModelClass(Class modelClass) {
        modelClass == simulationProfilePaneModel.settingsPaneModel.modelClass
    }

    SimulationSettingsPaneModel getSettingsPaneModel() {
        simulationProfilePaneModel.settingsPaneModel
    }
}



