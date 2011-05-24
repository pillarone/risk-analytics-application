package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

class ToggleKeyFigureAction extends AbstractKeyFigureAction {

    private IToggleValueProvider valueProvider
    final IFunction function

    ToggleKeyFigureAction(IFunction function, IToggleValueProvider valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(model, tree, function.getName())
        this.function = function
        this.valueProvider = valueProvider
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (valueProvider.functionEnabled()) {
            addFunction(function)
        } else {
            removeFunction(function)
        }
    }



}
