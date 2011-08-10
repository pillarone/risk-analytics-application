package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction

abstract class ParametrizedKeyFigureAction<E> extends AbstractKeyFigureAction {

    private List<? extends E> usedParameters
    protected IParametrizedKeyFigureValueProvider<? extends E> valueProvider

    ParametrizedKeyFigureAction(IParametrizedKeyFigureValueProvider<? extends E> valueProvider, AbstractModellingModel model, ULCTableTree tree, String key) {
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
        addFunction(function(valueProvider))
    }

    abstract protected IParametrizedFunction<? extends E> function(IParametrizedKeyFigureValueProvider<? extends E> valueProvider)
}
