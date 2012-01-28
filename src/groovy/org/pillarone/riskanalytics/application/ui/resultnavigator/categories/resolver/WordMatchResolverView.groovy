package org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryUtils

/**
 * @author martin.melchior
 */
class WordMatchResolverView extends ULCBoxPane {

    WordMatchResolver matcher

    WordMatchResolverView(WordMatchResolver matcher) {
        super(false)
        setMatcher(matcher)
        createView()
    }

    void updateMatcher(String value) {
        matcher.initialize(CategoryUtils.parseList(value))
    }

    private void createView() {
        ULCLabel label = new ULCLabel("Values to match: ")
        label.toolTipText = "Specify comma-separated list of possible values to match. Regular expression are supported."
        ULCTextField text = new ULCTextField(30)
        text.setEditable false
        text.setText(CategoryUtils.writeList(matcher.toMatch))

        this.add(ULCBoxPane.BOX_LEFT_TOP, label)
        this.add(ULCBoxPane.BOX_LEFT_TOP, text)

        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateMatcher(text.getText())
            }
        };
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        text.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
    }
}
