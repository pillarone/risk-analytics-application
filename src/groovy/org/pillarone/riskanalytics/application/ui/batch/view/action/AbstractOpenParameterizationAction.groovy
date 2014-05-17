package org.pillarone.riskanalytics.application.ui.batch.view.action
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

abstract class AbstractOpenParameterizationAction extends ResourceBasedAction {

    AbstractOpenParameterizationAction() {
        super('BatchOpenParameterizationAction')
    }

    @Override
    final void doActionPerformed(ActionEvent event) {
        if (enabled) {
            Parameterization parameterization = parameterization
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(parameterization)))
        }
    }

    protected abstract Parameterization getParameterization()
}
