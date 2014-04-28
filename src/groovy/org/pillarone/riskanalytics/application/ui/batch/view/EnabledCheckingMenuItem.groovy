package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCMenuItem

class EnabledCheckingMenuItem extends ULCMenuItem {

    EnabledCheckingMenuItem(IAction iAction) {
        super(iAction)
    }

    void updateEnablingState() {
        enabled = action.enabled
    }
}
