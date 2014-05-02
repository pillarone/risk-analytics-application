package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.shared.IUlcEventConstants
import org.pillarone.riskanalytics.application.ui.parameterization.action.TabStarter

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenMDPAction extends ResourceBasedAction {

    ULCTableTree tableTree

    public OpenMDPAction(ULCTableTree tableTree) {
        super("OpenMDPAction")
        this.tableTree = tableTree;
    }

    void doActionPerformed(ActionEvent event) {
        ActionEvent d = new ActionEvent(tableTree, String.valueOf(IUlcEventConstants.ACTION_PERFORMED))
        d.source = tableTree
        tableTree.getActionListeners().each {IActionListener actionListener ->
            if (actionListener instanceof TabStarter)
                actionListener.actionPerformed(d)
        }


    }
}
