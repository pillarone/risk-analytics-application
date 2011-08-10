package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.TvarFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

class TvarKeyFigureAction extends QuantilePerspectiveBasedKeyFigureAction<Double> {

    TvarKeyFigureAction(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(valueProvider, model, tree, "Tvar")
    }

    @Override
    protected IParametrizedFunction<? extends Double> function(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider) {
        return new TvarFunction(valueProvider.quantilePerspective, valueProvider.value)
    }


}
