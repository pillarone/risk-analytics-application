package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tree.TreePath
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.main.view.SelectionTreeView

abstract class AbstractSelectItemsInTreeAction<T> extends ResourceBasedAction {

    AbstractSelectItemsInTreeAction(String actionName) {
        super(actionName)
    }

    @Override
    final void doActionPerformed(ActionEvent event) {
        if (!enabled) {
            return
        }
        SelectionTreeView selectionTreeView = getSelectionTreeView()
        List<ItemNode> nodes = (items.collect { Object item ->
            TableTreeBuilderUtils.findNodeForItem(selectionTreeView.root, item)
        } - [null]) as List<ItemNode>
        if (nodes) {
            TreePath[] paths = nodes.collect { ItemNode node ->
                new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[])
            }
            TreePath[] parents = nodes.collect { ItemNode node ->
                new TreePath(DefaultTableTreeModel.getPathToRoot(node.parent) as Object[])
            }
            selectionTreeView.selectionTree.collapseAll()
            selectionTreeView.selectionTree.expandPaths(parents, false)
            selectionTreeView.selectionTree.selectionModel.selectionPaths = paths
        }
    }

    abstract protected List<T> getItems()

    private SelectionTreeView getSelectionTreeView() {
        Holders.grailsApplication.mainContext.getBean('selectionTreeView', SelectionTreeView)
    }
}
