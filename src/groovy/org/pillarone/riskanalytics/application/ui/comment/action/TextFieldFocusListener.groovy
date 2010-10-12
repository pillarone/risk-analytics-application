package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.FocusEvent
import com.ulcjava.base.application.event.IFocusListener
import com.ulcjava.base.application.util.Color

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TextFieldFocusListener implements IFocusListener {
    ULCTextField searchText
    String initialText

    public TextFieldFocusListener(ULCTextField searchText) {
        this.searchText = searchText;
        this.initialText = searchText.getText()
    }

    void focusGained(FocusEvent focusEvent) {
        String text = searchText.getText()
        if (initialText.equals(text)) {
            searchText.setText("")
            searchText.setForeground(Color.black)
        }

    }

    void focusLost(FocusEvent focusEvent) {
        String text = searchText.getText()
        if (!text || text == "") {
            searchText.setText(initialText)
            searchText.setForeground(Color.gray)
        }
    }

}
