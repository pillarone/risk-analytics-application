package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MockCompareSimulationViewModel extends CompareSimulationsViewModel {
    def MockCompareSimulationViewModel() {
        super(null, null, null);
        Simulation simulation1 = new Simulation("item1")
        Simulation simulation2 = new Simulation("item2")
        item = [simulation1, simulation2]
        selectionViewModel = new ItemsComboBoxModel([])
        buildTreeStructure()
    }


    ITableTreeModel buildTreeStructure() {
        SimpleTableTreeNode rootNode = new SimpleTableTreeNode("root")
        ResultStructureTableTreeNode child = new ResultStructureTableTreeNode("child", ApplicationModel)
        child.metaClass.getCellValue = {i ->
            i
        }
        rootNode.add(child)
        String[] strings = ["name", "one", "two"].toArray()
        MockTableTreeModel model = new MockTableTreeModel(rootNode, strings)
        treeModel = model//new FilteringTableTreeModel(model, filter)
//        return model
    }

    public getColumnName(int index) {
    }


}

class MockTableTreeModel extends DefaultTableTreeModel {
    IDataType numberDataType = new ULCNumberDataType()
    Closure setReferencedSimulationClosure = {selectedObejct ->}

    def MockTableTreeModel(iTableTreeNode, strings) {
        super(iTableTreeNode, strings);
    }


    boolean isHidden(int i) {
        return false
    }

    int getSimulationRunIndex(int column) {
        return column - 1
    }

    def Object getValueAt(def node, int i) {

        if (i == 0) {
            return node.getValueAt(0)
        } else {
            return i
        }
    }

    void setReferenceSimulation(Object selectedObject) {
        setReferencedSimulationClosure.call(selectedObject)
    }

    void clearCache() {
    }

    void nodeChanged(TreePath path, int index) {
    }

}