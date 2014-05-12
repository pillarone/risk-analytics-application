package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCMenuItem
import groovy.transform.CompileStatic

@CompileStatic
class EnabledCheckingMenuItem extends ULCMenuItem {

    EnabledCheckingMenuItem(IAction iAction) {
        super(iAction)
    }

    void updateEnablingState() {
        enabled = action.enabled
    }
}
