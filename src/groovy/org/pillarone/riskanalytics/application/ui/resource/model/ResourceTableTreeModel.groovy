package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractCommentableItemTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterObjectParameterTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedTableTreeModel


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


}
