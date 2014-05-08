package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.workflow.DealLinkDialog
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ChooseDealAction extends SingleItemAction {

    public ChooseDealAction(ULCTableTree tree) {
        super("ChooseDeal", tree)
    }

    void doActionPerformed(ActionEvent event) {

        if( quitWithAlertIfCalledWhenDisabled() ){
            return
        }

        DealLinkDialog dialog = new DealLinkDialog(UlcUtilities.getWindowAncestor(tree))
        Parameterization parameterization = getSelectedItem()
        if (!parameterization.isLoaded()) {
            parameterization.load()
        }
        Closure okAction = {
            ExceptionSafe.protect {
                parameterization.dealId = dialog.dealSelectionModel.dealId
                parameterization.valuationDate = dialog.valuationDatePaneModel.valuationDate
                parameterization.save()
            }
        }
        dialog.selectDeal parameterization
        dialog.okAction = okAction
        dialog.show()
    }


}

