package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.pillarone.riskanalytics.application.ui.main.action.OpenItemAction
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
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
        delegate(getSelectedItem(), event)
    }

    protected void delegate(def item, ActionEvent event) {

    }

    protected void delegate(ModellingItem item,ActionEvent  event) {
        openItemAction.doActionPerformed(event)
    }

    protected void delegate(BatchRun item,ActionEvent  event) {
        openBatchAction.doActionPerformed(event)
    }


}
