package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.AddTagDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagsAction extends SelectionTreeAction {


    def TagsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("TagsAction", tree, model);
    }

    void doActionPerformed(ActionEvent event) {
        AddTagDialog dialog = new AddTagDialog(tree, (ModellingUIItem) getSelectedUIItem())
        dialog.init()
        dialog.dialog.visible = true
    }
}
