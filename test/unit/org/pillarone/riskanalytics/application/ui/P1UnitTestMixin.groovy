package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.*

class P1UnitTestMixin {

    static ULCFrameOperator mainFrameOperator
    public static final String MAIN_FRAME = 'mainFrame'

    static void inTestFrame(ULCComponent component) {
        mainFrameOperator = null
        ULCFrame frame
        frame = new ULCFrame()
        frame.title = MAIN_FRAME
        frame.name = MAIN_FRAME
        frame.setSize(1024, 768)
        ULCBoxPane contentPane = new ULCBoxPane()
        frame.contentPane = contentPane
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, component)
        frame.visible = true
    }

    static ULCFrameOperator getMainFrameOperator() {
        if (mainFrameOperator == null) {
            mainFrameOperator = new ULCFrameOperator(new ComponentByNameChooser(MAIN_FRAME))
        }
        return mainFrameOperator;
    }

    static ULCTableTreeOperator getTableTreeOperatorByName(String name) {
        new ULCTableTreeOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    static ULCTextFieldOperator getTextFieldOperator(String name) {
        new ULCTextFieldOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    static ULCButtonOperator getButtonOperator(String name) {
        new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    static ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    static ULCCheckBoxOperator getCheckBoxOperator(String name) {
        new ULCCheckBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    static ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }
}
