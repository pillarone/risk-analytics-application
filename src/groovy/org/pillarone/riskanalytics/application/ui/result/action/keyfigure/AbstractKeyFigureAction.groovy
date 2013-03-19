package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

abstract class AbstractKeyFigureAction extends ResourceBasedAction {

    //TODO: introduce a common result view model for compare and single results
    AbstractModellingModel model
    ULCTableTree tree

    public AbstractKeyFigureAction(AbstractModellingModel model, ULCTableTree tree, String key) {
        super(key)
        this.model = model
        this.tree = tree
    }

    protected void addFunction(IFunction function) {
        model.addFunction(function)
    }

    protected void removeFunction(IFunction function) {
        model.removeFunction(function)
    }
}
