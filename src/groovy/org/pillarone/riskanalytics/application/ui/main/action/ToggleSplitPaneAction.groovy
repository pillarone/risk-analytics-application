package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.event.ActionEvent

class ToggleSplitPaneAction extends AbstractAction {
    public static final BigDecimal TOGGLED_TOLERANCE = 0.01
    private final ULCSplitPane pane
    private final double toggleValue

    ToggleSplitPaneAction(ULCSplitPane pane, String text, double toggleValue = 0) {
        super(text)
        this.toggleValue = toggleValue
        this.pane = pane
    }

    void actionPerformed(ActionEvent event) {
        if (toggled) {
            pane.setDividerLocation(pane.lastDividerLocation)
        } else {
            pane.setDividerLocation(toggleValue)
        }
    }

    private boolean isToggled() {
        Math.abs(toggleValue - pane.dividerLocationRelative) < TOGGLED_TOLERANCE
    }
}

