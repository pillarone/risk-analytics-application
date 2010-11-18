package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.AddTagDialog

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagsAction extends SelectionTreeAction {


    def TagsAction(ULCTableTree tree, P1RATModel model) {
        super("TagsAction", tree, model);
    }

    void doActionPerformed(ActionEvent event) {
        new AddTagDialog(tree, model.selectionTreeModel, getSelectedItem()).dialog.visible = true
    }
}
