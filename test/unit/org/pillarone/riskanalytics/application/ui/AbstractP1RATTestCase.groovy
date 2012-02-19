package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.testframework.operator.*
import com.ulcjava.base.application.ULCCheckBox
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractP1RATTestCase extends AbstractSimpleStandaloneTestCase {

    private static Log LOG = LogFactory.getLog(AbstractP1RATTestCase)

    ULCFrame frame
    ULCFrameOperator mainFrameOperator

    public void start() {
        LocaleResources.setTestMode()
        frame = new ULCFrame()
        frame.setTitle("mainFrame")
        frame.setName("mainFrame")
        frame.setSize(1024, 768)
        ULCBoxPane contentPane = new ULCBoxPane()
        frame.contentPane = contentPane
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, createContentPane())
        frame.setVisible true
    }

    @Override
    protected void setUp() {
        try {
            super.setUp()
        } catch (Exception e) {
            LOG.error("Setup failed", e)
            throw e;
        }
    }



    ULCFrameOperator getMainFrameOperator() {
        if (mainFrameOperator == null) {
            mainFrameOperator = new ULCFrameOperator(new ComponentByNameChooser("mainFrame"))
        }
        return mainFrameOperator;
    }

    ULCTableTreeOperator getTableTreeOperatorByName(String name) {
        new ULCTableTreeOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCTextFieldOperator getTextFieldOperator(String name) {
        new ULCTextFieldOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCButtonOperator getButtonOperator(String name) {
        new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCToggleButtonOperator getToggleButtonOperator(String name) {
        new ULCToggleButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCCheckBoxOperator getCheckBoxOperator(String name) {
        new ULCCheckBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCPopupMenuOperator getPopupMenuOperator(String name) {
        return new ULCPopupMenuOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCSpinnerOperator getSpinnerOperator(String name) {
        new ULCSpinnerOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    abstract ULCComponent createContentPane()


}
