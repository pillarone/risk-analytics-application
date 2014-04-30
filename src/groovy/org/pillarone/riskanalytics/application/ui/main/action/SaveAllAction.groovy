package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class SaveAllAction extends ResourceBasedAction {

    SaveAllAction() {
        super("SaveAll")
    }


    void doActionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            detailViewManager.saveAllOpenItems()
        }
    }

    DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

}