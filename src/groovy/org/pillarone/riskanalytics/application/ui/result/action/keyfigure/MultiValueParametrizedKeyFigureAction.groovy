package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import com.ulcjava.base.application.ULCTableTree

abstract class MultiValueParametrizedKeyFigureAction<E> extends AbstractKeyFigureAction {

    private List<? extends E> usedParameters
    protected IParametrizedKeyFigureValueProvider<List<? extends E>> valueProvider

    MultiValueParametrizedKeyFigureAction(IParametrizedKeyFigureValueProvider<List<? extends E>> valueProvider, AbstractModellingModel model, ULCTableTree tree, String key) {
        super(model, tree, key)
        usedParameters = []
        this.valueProvider = valueProvider
    }

    protected void addFunction(IParametrizedFunction<? extends E> function) {
        E parameter = function.parameter
        if (!usedParameters.contains(parameter)) {
            addFunction(function as IFunction)
            usedParameters.add(parameter)
        }
    }

    protected void removeFunction(IParametrizedFunction<? extends E> function) {
        E parameter = function.parameter
        if (usedParameters.contains(parameter)) {
            removeFunction(function as IFunction)
            usedParameters.remove(parameter)
        }
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        for(E value in valueProvider.value) {
            addFunction(function(value))
        }
    }

    abstract protected IParametrizedFunction<? extends E> function(E value)

}
