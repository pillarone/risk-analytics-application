package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.action.workflow.DealLinkDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fazl.rahman@art-allianz.com
 *
 * Introduce class to hold common behaviour of actions that
 * only operate on a single item.
 *
 */
abstract class SingleItemAction extends SelectionTreeAction {

    // Ugly constructor because TreeDoubleClickAction somehow calls OpenItemAction which somehow calls
    // e.g. CreateNewMajorVersion (our subclass) with a single string ctor.
    //
    public SingleItemAction(String name, ULCTableTree tree = null, RiskAnalyticsMainModel model = null) {
        super(name, tree, model)
    }

    // Current approach to enable menu only when one item is selected :-
    //
    // 1) register menuitem itself as a tree selection listener
    // 2) menuitem query this method on tree selection events
    //
    // The EnabledCheckingMenuItem encapsulates this behaviour.
    //
    @Override
    boolean isEnabled() {
        if (getAllSelectedObjectsSimpler().size() != 1) {
            return false
        }
        return super.isEnabled()//generic checks like user roles
    }

}

