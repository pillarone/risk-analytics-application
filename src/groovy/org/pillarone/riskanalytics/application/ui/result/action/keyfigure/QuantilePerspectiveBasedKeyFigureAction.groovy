package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.dataaccess.function.AbstractQuantilePerspectiveBasedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.core.output.QuantilePerspective

abstract class QuantilePerspectiveBasedKeyFigureAction<E> extends ParametrizedKeyFigureAction<E> {

    private List<KeyFigureDescriptor> usedParameters

    QuantilePerspectiveBasedKeyFigureAction(IQuantileBasedKeyFigureValueProvider<? extends E> valueProvider, AbstractModellingModel model, ULCTableTree tree, java.lang.String key) {
        super(valueProvider, model, tree, key)
        usedParameters = []
    }

    @Override
    protected IParametrizedFunction<? extends E> function(IParametrizedKeyFigureValueProvider<? extends E> valueProvider) {
        assert valueProvider instanceof IQuantileBasedKeyFigureValueProvider
        return function(valueProvider as IQuantileBasedKeyFigureValueProvider)
    }

    protected void addFunction(IParametrizedFunction<? extends E> function) {
        function = function as AbstractQuantilePerspectiveBasedFunction<? extends E>
        KeyFigureDescriptor<? extends E> descriptor = new KeyFigureDescriptor<? extends E>(function.quantilePerspective, function.parameter)
        if (!usedParameters.contains(descriptor)) {
            addFunction(function as IFunction)
            usedParameters.add(descriptor)
        }
    }

    protected void removeFunction(IParametrizedFunction<? extends E> function) {
        function = function as AbstractQuantilePerspectiveBasedFunction<? extends E>
        KeyFigureDescriptor<? extends E> descriptor = new KeyFigureDescriptor<? extends E>(function.quantilePerspective, function.parameter)
        if (!usedParameters.contains(descriptor)) {
            removeFunction(function as IFunction)
            usedParameters.remove(descriptor)
        }
    }

    abstract protected IParametrizedFunction<? extends E> function(IQuantileBasedKeyFigureValueProvider<? extends E> valueProvider)

    private static class KeyFigureDescriptor<E> {

        private E value
        private QuantilePerspective quantilePerspective


        KeyFigureDescriptor(QuantilePerspective quantilePerspective, E value) {
            this.quantilePerspective = quantilePerspective
            this.value = value
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof KeyFigureDescriptor) {
                return obj.value == value && obj.quantilePerspective == quantilePerspective
            }
            return false
        }

        @Override
        int hashCode() {
            return new HashCodeBuilder().append(value).append(quantilePerspective).toHashCode()
        }
    }

}
