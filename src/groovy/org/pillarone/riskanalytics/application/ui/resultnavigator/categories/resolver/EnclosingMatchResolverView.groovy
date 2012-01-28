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
        ULCLabel labelPrefix = new ULCLabel("Prefix to match: ")
        labelPrefix.toolTipText = "Specify comma-separated list of possible values to match. Regular expression are supported."
        ULCTextField textPrefix = new ULCTextField(30)
        textPrefix.setEditable false
        textPrefix.setText(CategoryUtils.writeList(matcher.prefix))

        ULCLabel labelSuffix = new ULCLabel("Suffix to match: ")
        labelSuffix.toolTipText = "Specify comma-separated list of possible values to match. Regular expression are supported."
        ULCTextField textSuffix = new ULCTextField(30)
        textSuffix.setEditable false
        textSuffix.setText(CategoryUtils.writeList(matcher.suffix))

        this.add(ULCBoxPane.BOX_LEFT_TOP, labelPrefix)
        this.add(ULCBoxPane.BOX_RIGHT_TOP, textPrefix)
        this.add(ULCBoxPane.BOX_LEFT_BOTTOM, labelSuffix)
        this.add(ULCBoxPane.BOX_RIGHT_BOTTOM, textSuffix)

        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateMatcher(textPrefix.getText(), textSuffix.getText())
            }
        };
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        textPrefix.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        textSuffix.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);

    }
}
