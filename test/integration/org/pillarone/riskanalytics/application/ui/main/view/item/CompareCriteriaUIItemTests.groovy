package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCFrameOperator

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareCriteriaUIItemTests extends AbstractCompareSimulationUIItemTests {

    public void testCriteria() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        ULCTableTreeOperator tableTreeOperator = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser('resultDescriptorTreeContent'))
        Thread.sleep 5000
        assertEquals new Double(22.2), tableTreeOperator.getValueAt(4, 1)
        assertEquals new Double(33.3), tableTreeOperator.getValueAt(7, 0)
        assertEquals new Double(66.6), tableTreeOperator.getValueAt(7, 1)

        changeSelection(frameOperator, 1)
        Thread.sleep 5000
        assertEquals new Double(22.2), tableTreeOperator.getValueAt(4, 0)
        assertEquals new Double(33.3), tableTreeOperator.getValueAt(7, 1)
        assertEquals new Double(66.6), tableTreeOperator.getValueAt(7, 0)

    }

    private void printValues(ULCTableTreeOperator tableTreeOperator, String name) {
        println("-------------------begin ${name} ${tableTreeOperator.getColumnCount()}-----------------")
        for (int row = 0; row < tableTreeOperator.getRowCount(); row++) {
            for (int column = 0; column < tableTreeOperator.getColumnCount(); column++) {
                println "tableTreeOperator.getValueAt(row,column) = ${row},${column},${tableTreeOperator.getValueAt(row, column)}"
            }
        }
        println("-------------------end ${name}-----------------")
    }
}
