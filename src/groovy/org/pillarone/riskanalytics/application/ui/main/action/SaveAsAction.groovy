package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SaveAsAction extends SingleItemAction {

    public SaveAsAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("SaveAs", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {

        if( quitWithAlertIfCalledWhenDisabled() ){
            return
        }

        ModellingUIItem selectedUIItem = (ModellingUIItem) getSelectedUIItem()
        NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), selectedUIItem)
        dialog.title = dialog.getText("title")
        dialog.okAction = { String name ->
            selectedUIItem.addItem(selectedUIItem, name)
        }
        dialog.show()
    }

}
