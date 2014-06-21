package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fazl.rahman@art-allianz.com
 */
class ShowPropertiesAction extends ResourceBasedAction {
    private static Log LOG = LogFactory.getLog(ShowPropertiesAction)

    ULCTableTree tree

    ShowPropertiesAction(ULCTableTree tree) {
        super("ShowPropertiesAction");
        this.tree = tree
    }

    void doActionPerformed(ActionEvent event) {
        int count = SelectionTreeAction.getSelectedUIItems(tree)?.size()
        String msg = "${count} items selected"
        LOG.info(msg)
        UIUtils.showAlert( UlcUtilities.getWindowAncestor(tree),
                msg, "Have a nice day..",
                ULCAlert.INFORMATION_MESSAGE)
    }


}
