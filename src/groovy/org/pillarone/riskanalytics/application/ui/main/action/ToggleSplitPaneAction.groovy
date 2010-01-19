package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class ToggleSplitPaneAction extends AbstractAction {
    ULCSplitPane pane
    double dividerLocation
    boolean collapsed

    ToggleSplitPaneAction(ULCSplitPane pane, String text) {
        super(text)
        this.@pane = pane
    }

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            if (collapsed) {
                pane.setDividerLocation(dividerLocation)
            } else {
                dividerLocation = pane.getDividerLocationRelative()
                pane.setDividerLocation(0.0)
            }
            collapsed = !collapsed
        }
    }
}
