package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.*
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractP1RATTestCase extends AbstractSimpleStandaloneTestCase {

    private static final Log LOG = LogFactory.getLog(AbstractP1RATTestCase)

    ULCFrame frame
    ULCFrameOperator mainFrameOperator

    public void start() {
        LocaleResources.testMode = true
        frame = new ULCFrame()
        frame.title = "mainFrame"
        frame.name = "mainFrame"
        frame.setSize(1024, 768)
        ULCBoxPane contentPane = new ULCBoxPane()
        frame.contentPane = contentPane
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, createContentPane())
        frame.visible = true
    }

    @Override
    protected void setUp() {
        try {
            ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
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

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    abstract ULCComponent createContentPane()
}
