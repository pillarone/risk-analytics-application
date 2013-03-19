package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.AbstractQuantilePerspectiveBasedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

abstract class QuantilePerspectiveBasedKeyFigureAction<E> extends ParametrizedKeyFigureAction<E> {


    QuantilePerspectiveBasedKeyFigureAction(IQuantileBasedKeyFigureValueProvider<? extends E> valueProvider, AbstractModellingModel model, ULCTableTree tree, java.lang.String key) {
        super(valueProvider, model, tree, key)
    }

    @Override
    protected IParametrizedFunction<? extends E> function(IParametrizedKeyFigureValueProvider<? extends E> valueProvider) {
        assert valueProvider instanceof IQuantileBasedKeyFigureValueProvider
        return function(valueProvider as IQuantileBasedKeyFigureValueProvider)
    }

    protected void addFunction(IParametrizedFunction<? extends E> function) {
        addFunction(function as IFunction)
    }

    protected void removeFunction(IParametrizedFunction<? extends E> function) {
        function = function as AbstractQuantilePerspectiveBasedFunction<? extends E>
        removeFunction(function as IFunction)
    }

    abstract protected IParametrizedFunction<? extends E> function(IQuantileBasedKeyFigureValueProvider<? extends E> valueProvider)

}
