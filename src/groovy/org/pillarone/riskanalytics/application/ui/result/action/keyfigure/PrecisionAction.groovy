package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

class PrecisionAction extends ResourceBasedAction {
    def model
    int adjustment

    public PrecisionAction(model, int adjustment, String label) {
        super(label)
        this.model = model
        this.adjustment = adjustment
    }

    public void doActionPerformed(ActionEvent event) {
        model.adjust(adjustment)
    }
}
