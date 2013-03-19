package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTableTreeOperator

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareFunctionUIItemTests extends AbstractCompareSimulationUIItemTests {

    public void testFunctions() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        Thread.sleep 10000

        ULCTableTreeOperator tableTreeOperator = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser('resultDescriptorTreeContent'))

        addResultFunction(frameOperator, 'percentileButton')
        assertEquals 4, tableTreeOperator.getColumnCount()

        addResultFunction(frameOperator, 'varButton')
        assertEquals 6, tableTreeOperator.getColumnCount()

        addResultFunction(frameOperator, 'tVarButton')
        assertEquals 8, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'minButton')
        assertEquals 10, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'maxButton')
        assertEquals 12, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'sigmaButton')
        assertEquals 14, tableTreeOperator.getColumnCount()
    }
}
