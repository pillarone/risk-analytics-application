package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCCardPane


class MainCardSelectionAction extends AbstractAction {

    String title
    ULCCardPane cardPane

    MainCardSelectionAction(String title, ULCCardPane cardPane) {
        super(title)
        this.title = title
        this.cardPane = cardPane
    }

    void actionPerformed(ActionEvent actionEvent) {
        cardPane.setSelectedName(title)
    }


}
