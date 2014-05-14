package org.pillarone.riskanalytics.application.ui.simulation.model.impl
import com.google.common.eventbus.Subscribe
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class SimulationConfigurationModel {

    SimulationProfilePaneModel simulationProfilePaneModel

    SimulationConfigurationModel(Class modelClass) {
        initSubModels(modelClass)
        attachListener()
    }

    private RiskAnalyticsEventBus getRiskAnalyticsEventBus() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsEventBus', RiskAnalyticsEventBus)
    }

    protected void attachListener() {
        getRiskAnalyticsEventBus().register(this)
    }

    void close() {
        getRiskAnalyticsEventBus().unregister(this)
    }

    protected initSubModels(Class modelClass) {
        simulationProfilePaneModel = new SimulationProfilePaneModel(modelClass)
    }

    void setParameterization(Parameterization parameterization) {
        if (parameterization) {
            settingsPaneModel.selectedParameterization = parameterization
        }
    }

    void setTemplate(ResultConfiguration template) {
        if (template) {
            settingsPaneModel.selectedResultConfiguration = template
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



