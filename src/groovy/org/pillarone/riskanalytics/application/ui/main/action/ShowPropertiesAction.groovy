package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
/**
 * @author fazl.rahman@art-allianz.com
 */
class ShowPropertiesAction extends SelectionTreeAction {


    ShowPropertiesAction(ULCTableTree tree) {
        super("ShowPropertiesAction", tree);
    }

    void doActionPerformed(ActionEvent event) {
        showInfoAlert("Selection properties", "Selected ${getSelectedUIItems()?.size()} items", true)
    }
}
