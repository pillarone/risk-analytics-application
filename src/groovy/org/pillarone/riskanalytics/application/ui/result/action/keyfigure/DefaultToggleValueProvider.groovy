package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCAbstractButton

class DefaultToggleValueProvider implements IToggleValueProvider {

    private ULCAbstractButton button

    DefaultToggleValueProvider(ULCAbstractButton button) {
        this.button = button
    }

    boolean functionEnabled() {
        return button.selected
    }


}
