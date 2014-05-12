package org.pillarone.riskanalytics.application.ui.batch.action
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

abstract class AbstractOpenItemAction<T extends ModellingItem> extends ResourceBasedAction {

    AbstractOpenItemAction(String actionName) {
        super(actionName)
    }

    @Override
    final void doActionPerformed(ActionEvent event) {
        if (enabled) {
            ModellingItem modellingItem = modellingItem
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(UIItemFactory.createItem(modellingItem)))
        }
    }

    protected abstract T getModellingItem()
}
