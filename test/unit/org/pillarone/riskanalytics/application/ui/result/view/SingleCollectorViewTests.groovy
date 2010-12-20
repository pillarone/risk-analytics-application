package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel

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
        SingleValueCollectorTableTreeModel model = new SingleValueCollectorTableTreeModel()
        model.singleValueResults = [["path0", 0, "x", 0], ["path1", 1, "x", 1], ["path2", 2, "x", 2], ["path3", 3, "x", 3], ["path4", 4, "x", 4], ["path5", 5, "x", 5], ["path6", 6, "x", 6], ["path7", 7, "x", 7], ["path8", 8, "x", 8], ["path9", 9, "x", 9]]
        model.iterations = 10
        return model
    }


}
