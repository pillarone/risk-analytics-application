package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction


class VarKeyFigureAction extends QuantilePerspectiveBasedKeyFigureAction<Double> {

    VarKeyFigureAction(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(valueProvider, model, tree, "Var")
    }

    @Override
    protected IParametrizedFunction<? extends Double> function(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider) {
        return new VarFunction(valueProvider.value, valueProvider.quantilePerspective)
    }


}
