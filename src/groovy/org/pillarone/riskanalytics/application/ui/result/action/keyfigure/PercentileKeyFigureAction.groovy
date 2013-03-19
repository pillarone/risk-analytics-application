package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

class PercentileKeyFigureAction extends QuantilePerspectiveBasedKeyFigureAction<Double> {

    PercentileKeyFigureAction(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider, AbstractModellingModel model, ULCTableTree tree) {
        super(valueProvider, model, tree, "Percentile")
    }

    @Override
    protected IParametrizedFunction<? extends Double> function(IQuantileBasedKeyFigureValueProvider<? extends Double> valueProvider) {
        return new PercentileFunction(valueProvider.value, valueProvider.quantilePerspective)
    }


}
