package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.TreePath
import groovy.transform.TypeChecked
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ModelNode
import org.pillarone.riskanalytics.application.ui.base.model.ResourceClassNode
import org.pillarone.riskanalytics.application.ui.base.model.ResourceGroupNode
import org.pillarone.riskanalytics.application.ui.main.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * delegate an action to openBatchAction or openItemAction
 * by double clicking
 * @author fouad.jaada@intuitive-collaboration.com
 */
@TypeChecked
class TreeDoubleClickAction extends SelectionTreeAction {

    OpenBatchAction openBatchAction
    OpenItemAction openItemAction

    public TreeDoubleClickAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("Open", tree, model)
        this.openBatchAction = new OpenBatchAction(tree, model);
        this.openItemAction = new OpenItemAction(tree, model);
    }

    void doActionPerformed(ActionEvent event) {
        def path = tree.selectedPath
        if (isNodeWeWantToToggleOnDoubleClick(path.lastPathComponent)) {
            toggle(path)
        } else {
            delegate(selectedItem, event)
        }
    }

    boolean isNodeWeWantToToggleOnDoubleClick(Object node) {
        [
                ModelNode,
                BatchRootNode,
                ResourceGroupNode,
                ItemGroupNode,
                ResourceClassNode
        ].any { Class clazz -> clazz.isInstance(node) }
    }

    protected void delegate(def item, ActionEvent event) {

    }

    def toggle(TreePath path) {
        tree.isExpanded(path) ? tree.collapsePath(path) : tree.expandPath(path)
    }

    protected void delegate(ModellingItem item, ActionEvent event) {
        openItemAction.doActionPerformed(event)
    }

    protected void delegate(BatchRun item, ActionEvent event) {
        openBatchAction.doActionPerformed(event)
    }


}
