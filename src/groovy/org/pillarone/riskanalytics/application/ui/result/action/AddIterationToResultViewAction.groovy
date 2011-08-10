package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.SingleIterationFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.IParametrizedKeyFigureValueProvider
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.MultiValueParametrizedKeyFigureAction

class AddIterationToResultViewAction extends MultiValueParametrizedKeyFigureAction<Integer> {

    AddIterationToResultViewAction(IParametrizedKeyFigureValueProvider<? extends List<Integer>> valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(valueProvider, model, tree, "ShowIterationInTreeView")
    }

    @Override
    protected IParametrizedFunction<? extends Integer> function(Integer value) {
        return new SingleIterationFunction(value)
    }


}
