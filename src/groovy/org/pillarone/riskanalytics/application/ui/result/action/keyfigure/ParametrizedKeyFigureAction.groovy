package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.dataaccess.function.IParametrizedFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel

abstract class ParametrizedKeyFigureAction<E> extends AbstractKeyFigureAction {

    protected IParametrizedKeyFigureValueProvider<? extends E> valueProvider

    ParametrizedKeyFigureAction(IParametrizedKeyFigureValueProvider<? extends E> valueProvider, AbstractModellingModel model, ULCTableTree tree, String key) {
        super(model, tree, key)
        this.valueProvider = valueProvider
    }

    protected void addFunction(IParametrizedFunction<? extends E> function) {
        addFunction(function as IFunction)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        addFunction(function(valueProvider))
    }

    abstract protected IParametrizedFunction<? extends E> function(IParametrizedKeyFigureValueProvider<? extends E> valueProvider)
}
