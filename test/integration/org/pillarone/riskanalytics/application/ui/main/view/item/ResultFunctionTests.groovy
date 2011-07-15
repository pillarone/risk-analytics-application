package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCToggleButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultFunctionTests extends AbstractResultUIItemTests {

    public void testView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        Thread.sleep(1000)

        ULCTableTreeOperator tableTreeOperator = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser('resultDescriptorTreeContent'))

        assertEquals 1, tableTreeOperator.getColumnCount()
        assertEquals new Double(11.1), tableTreeOperator.getValueAt(4, 0)
        assertEquals new Double(33.3), tableTreeOperator.getValueAt(7, 0)


        addResultFunction(frameOperator, 'percentileButton')
        assertEquals 2, tableTreeOperator.getColumnCount()
        assertEquals new Double(11.1), tableTreeOperator.getValueAt(4, 0)
        assertEquals new Double(1000.0), tableTreeOperator.getValueAt(4, 1)
        assertEquals new Double(33.3), tableTreeOperator.getValueAt(7, 0)
        assertEquals new Double(1000.0), tableTreeOperator.getValueAt(7, 1)

        addResultFunction(frameOperator, 'varButton')
        assertEquals 3, tableTreeOperator.getColumnCount()
        assertEquals new Double(250.0), tableTreeOperator.getValueAt(4, 2)
        assertEquals new Double(499.5), tableTreeOperator.getValueAt(7, 2)

        addResultFunction(frameOperator, 'tvarButton')
        assertEquals 4, tableTreeOperator.getColumnCount()
        assertEquals new Double(99249.99999999991), tableTreeOperator.getValueAt(4, 3)
        assertEquals new Double(99499.49999999991), tableTreeOperator.getValueAt(7, 3)

        addFunction(frameOperator, 'minButton')
        assertEquals 5, tableTreeOperator.getColumnCount()
        assertEquals new Double(1.0), tableTreeOperator.getValueAt(7, 4)

        addFunction(frameOperator, 'maxButton')
        assertEquals 6, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'sigmaButton')
        assertEquals 7, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'minButton')
        assertEquals 6, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'maxButton')
        assertEquals 5, tableTreeOperator.getColumnCount()

        addFunction(frameOperator, 'sigmaButton')
        assertEquals 4, tableTreeOperator.getColumnCount()
    }


}
