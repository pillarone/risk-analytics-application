package org.pillarone.riskanalytics.application.ui.batch.view.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

abstract class AbstractOpenParameterizationAction extends ResourceBasedAction {

    AbstractOpenParameterizationAction() {
        super('BatchOpenParameterizationAction')
    }

    @Override
    final void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Parameterization parameterization = parameterization
            Model model = parameterization.modelClass.newInstance() as Model
            riskAnalyticsMainModel.openItem(model, UIItemFactory.createItem(parameterization, model))
        }
    }

    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    protected abstract Parameterization getParameterization()
}
