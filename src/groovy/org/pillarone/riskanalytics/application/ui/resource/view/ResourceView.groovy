package org.pillarone.riskanalytics.application.ui.resource.view

import org.pillarone.riskanalytics.application.ui.base.view.AbstractParameterizationTreeView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel

class ResourceView extends AbstractParameterizationTreeView {

    ResourceView(ResourceViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
    }

    @Override
    protected String getRowHeaderTableTreeName() {
        return "resourceTreeRowHeader"
    }

    @Override
    protected String getViewPortTableTreeName() {
        return "resourceTreeContent"
    }


}
