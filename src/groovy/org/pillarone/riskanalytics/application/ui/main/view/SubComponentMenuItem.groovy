package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SubComponentMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    ResourceBasedAction basedAction

    public SubComponentMenuItem(ResourceBasedAction iAction) {
        super(iAction);
        basedAction = iAction
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(basedAction.isEnabled())
    }

}
