package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SaveAsAction extends SelectionTreeAction {

    public SaveAsAction(ULCTableTree tree, P1RATModel model) {
        super("SaveAs", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), getSelectedItem())
        dialog.title = dialog.getText("title")
        dialog.okAction = {  model.addItem(getSelectedItem(), dialog.nameInput.text) }
        dialog.show()
    }

}
