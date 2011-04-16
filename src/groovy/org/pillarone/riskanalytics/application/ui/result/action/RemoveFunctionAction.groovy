package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.ULCToggleButton
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RemoveFunctionAction extends AbstractAction {

    private ResultViewModel model
    private IFunction function
    private ULCToggleButton button

    public RemoveFunctionAction(ResultViewModel model, IFunction function, ULCToggleButton button) {
        super(UIUtils.getText(RemoveFunctionAction.class, "Remove", [function.getName(0)]));
        this.function = function
        this.model = model
        this.button = button
    }

    public void actionPerformed(ActionEvent event) {
        model.removeFunction(function)
        if (button != null) {
            button.selected = false
        }
    }

}
