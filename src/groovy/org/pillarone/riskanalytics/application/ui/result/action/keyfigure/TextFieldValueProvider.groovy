package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import com.ulcjava.base.application.ULCTextField

class TextFieldValueProvider<E> implements IParametrizedKeyFigureValueProvider<E> {

    private ULCTextField textField

    TextFieldValueProvider(ULCTextField textField) {
        this.textField = textField
    }

    E getValue() {
        return (E) textField.value
    }


}
