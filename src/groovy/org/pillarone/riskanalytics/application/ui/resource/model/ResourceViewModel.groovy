package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedViewModel
import org.pillarone.riskanalytics.core.simulation.item.Resource

class ResourceViewModel extends AbstractParametrizedViewModel {


    ResourceViewModel(Resource resource) {
        super(null, resource, null)
        periodCount = 1
    }

    @Override
    protected AbstractParametrizedTableTreeModel createTableTreeModel(Object builder) {
        return new ResourceTableTreeModel(builder)
    }

    @Override
    protected createTreeBuilder() {
        return new ResourceTreeBuilder(item)
    }


}
