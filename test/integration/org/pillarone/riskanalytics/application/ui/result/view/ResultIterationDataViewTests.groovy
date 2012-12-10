package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCComponentOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun

class ResultIterationDataViewTests extends AbstractSimpleFunctionalTest {

    void testDummy() {
        assertTrue true
    }

    ResultIterationDataViewModel model

    protected void doStart() {
        SimulationRun run = new SimulationRun()
        run.periodCount = 3
        model = new ResultIterationDataViewModel(run, [new ResultTableTreeNode("testNode testNode testNode testNode testNode testNode testNode ")], false, true, false, null)
        model.addCriteria 0
        model.addCriteria 0
        model.addCriteria 0
        model.addCriteria 0
        model.simulationRun.periodCount = 2
        ResultIterationDataView view = new ResultIterationDataView(model)

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.size = new Dimension(600, 400)
        frame.contentPane = view.content
        frame.setVisible(true)
    }

    void testOpenView() {
        ULCComponentOperator component = new ULCComponentOperator(new ULCFrameOperator("test"), new ComponentByNameChooser("iterationDataView"))
        assertNotNull component
        assertTrue component.getULCComponent() instanceof ULCBoxPane
    }

    void testCriteriaGroupPane() {
        int criteriaGroupCount = model.getCriteriaGroupCount()
        assertEquals 1, model.getCriteriaGroupCount()
        criteriaGroupCount.times {int groupIndex ->
            ULCComponentOperator component = new ULCComponentOperator(new ULCFrameOperator("test"), new ComponentByNameChooser("groupPane$groupIndex"))
            assertNotNull component


        }
    }

    void testCriteriaPane() {
        assertEquals 5, model.getCriteriaGroup(0).size()
        assertEquals 1, model.getCriteriaGroupCount()
        model.getCriteriaGroup(0).size().times {
            ULCComponentOperator component = new ULCComponentOperator(new ULCFrameOperator("test"), new ComponentByNameChooser("criteriaPaneG0$it"))
            assertNotNull component
        }
    }
}