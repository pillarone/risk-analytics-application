package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTableTreeOperator

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareKeyFigureUIItemTests extends AbstractCompareSimulationUIItemTests {

    public void testFunctions() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        Thread.sleep 1000

        ULCTableTreeOperator tableTreeOperator = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser('resultDescriptorTreeContent'))

        addKeyFigureFunction(frameOperator, "devPercentage")
        assertEquals 3, tableTreeOperator.getColumnCount()

        addKeyFigureFunction(frameOperator, "devPercentage")
        assertEquals 2, tableTreeOperator.getColumnCount()

        addKeyFigureFunction(frameOperator, "frAbsolute")
        assertEquals 3, tableTreeOperator.getColumnCount()

        addKeyFigureFunction(frameOperator, "frPercentage")
        assertEquals 4, tableTreeOperator.getColumnCount()

        addKeyFigureFunction(frameOperator, "devAbsolute")
        assertEquals 5, tableTreeOperator.getColumnCount()


    }
}
