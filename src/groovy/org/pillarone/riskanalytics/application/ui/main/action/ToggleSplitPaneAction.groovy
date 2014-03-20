package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class ToggleSplitPaneAction extends AbstractAction {
    private final ULCSplitPane pane
    private Double dividerLocation
    private final double toggleValue

    ToggleSplitPaneAction(ULCSplitPane pane, String text, double toggleValue = 0) {
        super(text)
        this.toggleValue = toggleValue
        this.pane = pane
    }

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            if (dividerLocation != null) {
                pane.setDividerLocation(dividerLocation)
                dividerLocation = null
            } else {
                dividerLocation = pane.dividerLocationRelative
                pane.setDividerLocation(toggleValue)
            }
        }
    }
}
