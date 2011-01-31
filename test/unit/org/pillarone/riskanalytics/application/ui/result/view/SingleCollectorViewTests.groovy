package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueTreeBuilder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorViewTests extends AbstractP1RATTestCase {

    SingleValueCollectorTableTreeModel singleValueCollectorTableTreeModel

    public void testView() {
        Thread.sleep 10000
    }

    ULCComponent createContentPane() {
        singleValueCollectorTableTreeModel = getMockModel()
        SingleCollectorView view = new SingleCollectorView(singleValueCollectorTableTreeModel)
        view.init()
        return view.content
    }

    private SingleValueCollectorTableTreeModel getMockModel() {
        SingleValueCollectorTableTreeModel model = new SingleValueCollectorTableTreeModel(null, null)
        model.singleValueResultsMap[0] = [[["net", 1.84783834, "paid", 1, 0], ["net", 11.04129301744273E7, "paid", 1, 0], ["net", 111.5637624293, "paid", 1, 0]]]
        model.singleValueResultsMap[1] = [[["net", 2.84783834, "paid", 1, 0], ["net", 22.04129301744273E7, "paid", 1, 0], ["net", 222.5637624293, "paid", 1, 0]]]
        model.iterations = 1
        model.periodCount = 1
        model.nodes = [new ResultTableTreeNode("node1"), new ResultTableTreeNode("node2")]
        model.metaClass.setIterations = {->
            model.iterations = 1
        }
        model.metaClass.init = {->
            model.builder = new SingleValueTreeBuilder(model.singleValueResultsMap, model.iterations, 2, 1)
            model.builder.build()
        }



        return model
    }


}
