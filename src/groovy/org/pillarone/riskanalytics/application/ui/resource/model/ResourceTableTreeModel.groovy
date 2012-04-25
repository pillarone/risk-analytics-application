package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedTableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.components.ResourceModelAdapter


class ResourceTableTreeModel extends AbstractParametrizedTableTreeModel {

    private ResourceTreeBuilder builder

    ResourceTableTreeModel(ResourceTreeBuilder builder) {
        super(builder.root)
        this.builder = builder
    }

    int getColumnCount() {
        return 2
    }

    @Override
    protected List<String> getAllEditablePaths() {
        return builder.item.allEditablePaths
    }

    @Override
    protected Model getSimulationModel() {
        return new ResourceModelAdapter(builder.resource)
    }


}
