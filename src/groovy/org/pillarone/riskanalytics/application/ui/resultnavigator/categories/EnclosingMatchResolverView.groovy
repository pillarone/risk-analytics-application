package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke

/**
 * @author martin.melchior
 */
class EnclosingMatchResolverView extends ULCBoxPane {

    EnclosingMatchResolver matcher

    EnclosingMatchResolverView(EnclosingMatchResolver matcher) {
        super(2,2)
        setMatcher(matcher)
        createView()
    }

    void updateMatcher(String prefixSpec, String suffixSpec) {
        List<String> prefixes = CategoryUtils.parseList(prefixSpec)
        List<String> suffixes = CategoryUtils.parseList(suffixSpec)
        matcher.initialize(prefixes, suffixes)
    }

    private void createView() {
        ULCLabel labelPre = new ULCLabel("Prefix to match: ")
        labelPre.toolTipText = "Specify comma-separated list of possible values to match. Regular expression are supported."
        ULCTextField textPre = new ULCTextField(60)
        textPre.setEditable true
        ULCLabel labelSuff = new ULCLabel("Suffix to match: ")
        labelSuff.toolTipText = "Specify comma-separated list of possible values to match. Regular expression are supported."
        ULCTextField textSuff = new ULCTextField(60)
        textSuff.setEditable true

        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateMatcher(textPre.getText(), textSuff.getText())
            }
        };
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

        textPre.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        textSuff.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);

        textPre.setText(CategoryUtils.writeList(matcher.prefix))
        textSuff.setText(CategoryUtils.writeList(matcher.suffix))

        this.add(ULCBoxPane.BOX_LEFT_TOP, labelPre)
        this.add(ULCBoxPane.BOX_RIGHT_TOP, textPre)
        this.add(ULCBoxPane.BOX_LEFT_BOTTOM, labelSuff)
        this.add(ULCBoxPane.BOX_RIGHT_BOTTOM, textSuff)
    }
}
