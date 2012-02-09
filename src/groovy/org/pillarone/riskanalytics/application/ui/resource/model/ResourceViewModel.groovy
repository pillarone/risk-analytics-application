package org.pillarone.riskanalytics.application.ui.resource.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.core.simulation.item.Resource

class ResourceViewModel extends AbstractCommentableItemModel {


    ResourceTableTreeModel treeModel

    ResourceViewModel(Resource resource) {
        super(null, resource, null)
        periodCount = 1
    }

    @Override
    protected ITableTreeModel buildTree() {
        treeModel = new ResourceTableTreeModel(new ResourceTreeBuilder(item))
        return treeModel
    }
}
