package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.main.action.SaveAction
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.comment.view.TabbedPaneChangeListener
import com.ulcjava.base.application.tabletree.ITableTreeModel


abstract class AbstractParametrizedViewModel extends AbstractCommentableItemModel {

    AbstractParametrizedTableTreeModel paramterTableTreeModel
    PropertiesViewModel propertiesViewModel

    List<ParameterValidation> validationErrors = []
    RiskAnalyticsMainModel mainModel

    AbstractParametrizedViewModel(Model model, ModellingItem item, ModelStructure modelStructure) {
        super(model, item, modelStructure)
        propertiesViewModel = new PropertiesViewModel(item)

    }

    protected ITableTreeModel buildTree() {
        builder = createTreeBuilder()
        paramterTableTreeModel = createTableTreeModel(builder)
        treeModel = paramterTableTreeModel //TODO
        paramterTableTreeModel.addValueChangedListener(
                [valueChanged: {Object node, int column -> item.changed = true}] as TableTreeValueChangedListener)
        paramterTableTreeModel.readOnly = !item.isEditable()
        return paramterTableTreeModel
    }

    abstract protected def createTreeBuilder()

    abstract protected AbstractParametrizedTableTreeModel createTableTreeModel(def builder)

    @Override
    IActionListener getSaveAction(ULCComponent parent) {
        return new SaveAction(parent, mainModel, mainModel?.getAbstractUIItem(item))
    }

    SimpleTableTreeNode findNodeForPath(String path) {
        String[] pathElements = path.split(":")
        SimpleTableTreeNode currentNode = paramterTableTreeModel.root
        for (String p in pathElements) {
            currentNode = currentNode?.getChildByName(p)
        }
        return currentNode
    }

    void addErrors(List<ParameterValidation> validationErrors) {
        for (ParameterValidation error in validationErrors) {
            ParameterizationTableTreeNode node = findNodeForPath(error.getPath())
            node.addError(error)
            paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
        for (ParameterValidation previousError in this.validationErrors) {
            ParameterValidation currentError = validationErrors.find { it.path == previousError.path}
            //Error is resolved now
            if (currentError == null) {
                ParameterizationTableTreeNode node = findNodeForPath(previousError.getPath())
                //if node is null, the error node was removed
                if (node != null) {
                    node.errors = null
                    paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
                }
            }
        }
        this.validationErrors = validationErrors
    }

    void setReadOnly(boolean value) {
        paramterTableTreeModel.readOnly = value
    }

    public boolean isReadOnly() {
        return paramterTableTreeModel.readOnly
    }

    void tabbedPaneChanged(CommentFilter filter) {
        tabbedPaneChangeListeners.each {TabbedPaneChangeListener listener ->
            listener.tabbedPaneChanged(filter)
        }
    }

    ITableTreeModel getTableTreeModel() {
        return paramterTableTreeModel
    }
}
