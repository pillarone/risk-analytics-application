package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCSpinnerOperator

import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest

class MultiDimensionalParameterViewTests extends AbstractSimpleFunctionalTest {

    AbstractMultiDimensionalParameter multiDimensionalParameter

    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        def mdp = new SimpleMultiDimensionalParameter([[1, 2, 3], [1, 2, 3], [1, 2, 3]])
        def node = ParameterizationNodeFactory.getNode([ParameterHolderFactory.getHolder("path", 0, mdp)], null)
        MultiDimensionalParameterModel model = new TestMultiDimensionalParameterModel(null, node, 1)
        multiDimensionalParameter = model.multiDimensionalParameterInstance
        frame.contentPane = new MultiDimensionalParameterView(model).content
        frame.visible = true
    }

    void testChangeDimension() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        ULCSpinnerOperator rowCount = new ULCSpinnerOperator(frame, new ComponentByNameChooser('rowCount'))
        rowCount.scrollToString('5', ScrollAdjuster.INCREASE_SCROLL_DIRECTION)

        ULCButtonOperator apply = new ULCButtonOperator(frame, "Apply")
        apply.getFocus()
        apply.clickMouse()

        assertEquals 5, multiDimensionalParameter.valueRowCount

        rowCount.scrollToString('2', ScrollAdjuster.DECREASE_SCROLL_DIRECTION)

        apply.getFocus()
        apply.clickMouse()


        assertEquals 2, multiDimensionalParameter.valueRowCount

        ULCSpinnerOperator colCount = new ULCSpinnerOperator(frame, new ComponentByNameChooser('columnCount'))
        colCount.scrollToString('5', ScrollAdjuster.INCREASE_SCROLL_DIRECTION)

        apply.getFocus()
        apply.clickMouse()


        assertEquals 5, multiDimensionalParameter.valueColumnCount

        colCount.scrollToString('1', ScrollAdjuster.DECREASE_SCROLL_DIRECTION)

        apply.getFocus()
        apply.clickMouse()

        assertEquals 1, multiDimensionalParameter.valueColumnCount

    }

}
