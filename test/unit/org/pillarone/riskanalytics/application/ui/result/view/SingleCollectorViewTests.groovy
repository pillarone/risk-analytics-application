package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueTreeBuilder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorViewTests extends AbstractP1RATTestCase {

    SingleValueCollectorTableTreeModel singleValueCollectorTableTreeModel
    int maxIteration = 201

    public void testView() {
//        Thread.sleep 1000
        ULCButtonOperator previousButton = getButtonOperator("SingleCollectorView.previousButton")
        ULCButtonOperator nextButton = getButtonOperator("SingleCollectorView.nextButton")
        assertFalse previousButton.isEnabled()
        assertTrue nextButton.isEnabled()

        ULCTextFieldOperator from = getTextFieldOperator("SingleCollectorView.fromTextField")
        ULCTextFieldOperator to = getTextFieldOperator("SingleCollectorView.toTextField")
        to.getFocus()
        to.clearText()
        // 300 greater than simulationRun.iterations 201
        to.typeText("300")
        ULCButtonOperator applyButton = getButtonOperator("SingleCollectorView.apply")


        applyButton.getFocus()
        applyButton.clickMouse()


        assertFalse previousButton.isEnabled()
        assertFalse nextButton.isEnabled()

        assertEquals String.valueOf(maxIteration), to.getText()

        from.getFocus()
        from.clearText()
        from.typeText("100")
        applyButton.getFocus()
        applyButton.clickMouse()

        assertTrue previousButton.isEnabled()
        assertFalse nextButton.isEnabled()

        previousButton.getFocus()
        previousButton.clickMouse()

        assertTrue nextButton.isEnabled()

    }



    ULCComponent createContentPane() {
        singleValueCollectorTableTreeModel = getMockModel()
        SingleCollectorView view = new SingleCollectorView(singleValueCollectorTableTreeModel)
        view.init()
        return view.content
    }

    private SingleValueCollectorTableTreeModel getMockModel() {
        SingleValueCollectorTableTreeModel model = new SingleValueCollectorTableTreeModel(null, null, false)
        model.singleValueResultsMap[0] = [[["net", 1.84783834, "paid", 1, 0], ["net", 11.04129301744273E7, "paid", 1, 0], ["net", 111.5637624293, "paid", 1, 0]]]
        model.singleValueResultsMap[1] = [[["net", 2.84783834, "paid", 1, 0], ["net", 22.04129301744273E7, "paid", 1, 0], ["net", 222.5637624293, "paid", 1, 0]]]
        model.iterations = 1
        model.periodCount = 1
        model.nodes = [new ResultTableTreeNode("node1"), new ResultTableTreeNode("node2")]
        model.metaClass.getMaxIteration = {->
            return maxIteration
        }
        model.metaClass.init = {->
            model.builder = new SingleValueTreeBuilder(model.singleValueResultsMap, 1, model.iterations, 2, 1)
            model.builder.build()
        }



        return model
    }


}
