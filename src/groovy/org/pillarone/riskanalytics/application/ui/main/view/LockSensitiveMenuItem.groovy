package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class LockSensitiveMenuItem extends ULCMenuItem implements ITreeSelectionListener {

    ResourceBasedAction basedAction

    public LockSensitiveMenuItem(ResourceBasedAction iAction) {
        super(iAction);
        this.basedAction = iAction;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        boolean b = basedAction.isEnabled()
        setEnabled(b)
    }
}
