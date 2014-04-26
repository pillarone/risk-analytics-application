package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import org.pillarone.riskanalytics.application.ui.main.action.OpenTransactionLinkAction
import com.ulcjava.base.application.event.TreeSelectionEvent

/**
 * Wrap the action and implement ITreeSelection listener to enable/disable depending on selection
 */

// TODO - DELETE THIS CLASS ONCE WE KNOW THE EnableCheckingMenuItem does the job.
@Deprecated
class OpenExternalMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    private OpenTransactionLinkAction openExternalAction

    public OpenExternalMenuItem(OpenTransactionLinkAction action) {
        super(action)
        this.openExternalAction = action
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(action.isEnabled())
    }
}
