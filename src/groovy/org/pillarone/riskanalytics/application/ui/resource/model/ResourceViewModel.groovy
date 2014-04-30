package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedViewModel
import org.pillarone.riskanalytics.core.simulation.item.Resource

class ResourceViewModel extends AbstractParametrizedViewModel {

    ResourceViewModel(Resource resource) {
        super(null, resource, null)
        resource.addListener(this)
        periodCount = 1
    }

    void close() {
        item.removeListener(this)
    }

    @Override
    protected AbstractParametrizedTableTreeModel createTableTreeModel(Object builder) {
        return new ResourceTableTreeModel(builder)
    }

    @Override
    protected createTreeBuilder() {
        return new ResourceTreeBuilder(item)
    }

    @Override
    Resource getItem() {
        return super.getItem() as Resource
    }
}
