package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedTableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.components.ResourceModelAdapter
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode


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

    @Override
    protected SimpleTableTreeNode findNode(String[] pathComponents) {
        return internalFindNode(getRoot(), pathComponents[0])
    }

    protected SimpleTableTreeNode internalFindNode(SimpleTableTreeNode current, String name) {
        if (current.name == name) {
            return current
        }
        for (int i = 0; i < current.childCount; i++) {
            SimpleTableTreeNode result = internalFindNode(current.getChildAt(i), name)
            if (result != null) {
                return result
            }
        }
        return null
    }
}
