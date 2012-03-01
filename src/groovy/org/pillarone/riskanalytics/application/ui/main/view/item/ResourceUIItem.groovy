package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Resource
import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel
import org.pillarone.riskanalytics.application.ui.resource.view.ResourceView

class ResourceUIItem extends ModellingUIItem {

    ResourceUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, Resource item) {
        super(mainModel, simulationModel, item)
    }

    @Override
    com.ulcjava.base.application.ULCContainer createDetailView() {
        return new ResourceView(getViewModel()).content
    }

    @Override
    Object getViewModel() {
        ResourceViewModel model = new ResourceViewModel(item)
        model.mainModel = mainModel
        return model
    }

    @Override
    Model getModel() {
        return null
    }


}
