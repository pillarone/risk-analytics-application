package org.pillarone.riskanalytics.application.ui.resource.view
import org.pillarone.riskanalytics.application.ui.base.view.AbstractParameterizationTreeView
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel

class ResourceView extends AbstractParameterizationTreeView {

    ResourceView(ResourceViewModel model) {
        super(model)
    }

    @Override
    void close() {
        super.close()
        model.close()
    }

    @Override
    ResourceViewModel getModel() {
        return super.getModel() as ResourceViewModel
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
