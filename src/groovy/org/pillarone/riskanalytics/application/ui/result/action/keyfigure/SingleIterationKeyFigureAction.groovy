package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.SingleIterationFunction


class SingleIterationKeyFigureAction extends ParametrizedKeyFigureAction<Integer>{

    public static final String SINGLE_ITERATION = "SingleIteration"

    SingleIterationKeyFigureAction(IParametrizedKeyFigureValueProvider<? extends Integer> valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(valueProvider, model, tree, SINGLE_ITERATION)
    }

    @Override
    protected IParametrizedFunction<? extends Integer> function(IParametrizedKeyFigureValueProvider<? extends Integer> valueProvider) {
        return new SingleIterationFunction(valueProvider.value)
    }


}
