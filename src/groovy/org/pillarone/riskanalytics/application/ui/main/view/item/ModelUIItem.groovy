package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener

class ModelUIItem extends AbstractUIItem {

    ModelUIItem(RiskAnalyticsMainModel mainModel, Model model) {
        super(mainModel, model)
    }

    String createTitle() {
        return model.class.simpleName
    }

    ULCContainer createDetailView() {
        throw new IllegalStateException("A model does not have a detail view.")
    }

    AbstractModellingModel getViewModel() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    Model getItem() {
        return model
    }

    void removeAllModellingItemChangeListener() {
        // intentionally blank (probably?)
    }

    void addModellingItemChangeListener(IModellingItemChangeListener listener) {
        // intentionally blank (probably?)
    }

}
