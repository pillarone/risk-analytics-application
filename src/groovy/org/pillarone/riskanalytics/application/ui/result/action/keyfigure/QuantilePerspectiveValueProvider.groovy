package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCTextField
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.ui.result.model.QuantileFunctionType

class QuantilePerspectiveValueProvider<E> extends TextFieldValueProvider<E> implements IQuantileBasedKeyFigureValueProvider<E> {

    protected ULCComboBox quantilePerspective

    QuantilePerspectiveValueProvider(ULCTextField textField, ULCComboBox quantilePerspective) {
        super(textField)
        this.quantilePerspective = quantilePerspective
    }

    QuantilePerspective getQuantilePerspective() {
        final QuantileFunctionType type = quantilePerspective.model.selectedEnum
        return type.quantilePerspective
    }


}
