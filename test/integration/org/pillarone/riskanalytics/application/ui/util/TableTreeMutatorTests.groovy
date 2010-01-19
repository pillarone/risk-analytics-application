package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

class TableTreeMutatorTests extends GroovyTestCase {

    void testBulkChange() {
        DefaultMutableTableTreeNode root = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[], [true, true, true] as boolean[])
        DefaultMutableTableTreeNode child = new DefaultMutableTableTreeNode([1.0, 2.0, 3.0] as Object[], [true, true, true] as boolean[])
        root.add(child)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["col1", "col2", "col3"] as String[])

        TableTreeMutator mutator = new TableTreeMutator(model)

        mutator.applyChanges([root, child], [["A", "B", "C"], [1.1, 2.2, 3.3]])

        assertEquals "A", root.getValueAt(0)
        assertEquals "B", root.getValueAt(1)
        assertEquals "C", root.getValueAt(2)

        assertEquals 1.1, child.getValueAt(0)
        assertEquals 2.2, child.getValueAt(1)
        assertEquals 3.3, child.getValueAt(2)
    }

    void testTransactionalBulkChange() {

        DefaultMutableTableTreeNode root = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[], [true, true, true] as boolean[])
        ParameterizationTableTreeNode child = new TableTreeMutatorTestNode([ParameterHolderFactory.getHolder("path1", 0, "value"), ParameterHolderFactory.getHolder("path2", 0, "value"), ParameterHolderFactory.getHolder("path3", 0, "value")], null)
        root.add(child)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["col1", "col2", "col3"] as String[])

        TableTreeMutator mutator = new TableTreeMutator(model)
        shouldFail(Exception, { mutator.applyChanges([root, child], [["A", "B", "C"], [1.1, 2.2, 3.3]]) })


        assertEquals "a", root.getValueAt(0)
        assertEquals "b", root.getValueAt(1)
        assertEquals "c", root.getValueAt(2)

        assertEquals "path1", child.getValueAt(0)
        assertEquals "path2", child.getValueAt(1)
        assertEquals "path3", child.getValueAt(2)

    }

    //todo: fix! does not work anymore because a setValueAt on PCTTN changes all child nodes of a POPTTN
    /*void testStructureChangeDetection() {
        IParameterObjectClassifier type = DistributionType.NORMAL

        ParameterObjectParameter parameter = new ParameterObjectParameter(path: 'testPath', periodIndex: 0)
        parameter.setParameterInstance(type, ["mean": 1, "stDev": 1])
        ParameterizationTableTreeNode rootNode = ParameterizationNodeFactory.getNode([parameter], null)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: rootNode, columnCount: 1)
        TableTreeMutator mutator = new TableTreeMutator(model)

        shouldFail(UnsupportedOperationException, { mutator.applyChanges([rootNode.getChildAt(0)], [["Constant"]], 1)})
        assertEquals "value not rollbacked", "Normal", rootNode.getChildAt(0).getValueAt(1)
    }*/

    void testDataFormatFailure() {
        DefaultMutableTableTreeNode root = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[], [true, true, true] as boolean[])
        DefaultMutableTableTreeNode child = new DefaultMutableTableTreeNode([1.0, 2.0, 3.0] as Object[], [true, false, true] as boolean[])
        root.add(child)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["col1", "col2", "col3"] as String[])

        TableTreeMutator mutator = new TableTreeMutator(model)

        shouldFail(IllegalArgumentException, {mutator.applyChanges([root, child], [["A", "B", "C"], [1.1, 2.2, 3.3]])})

        assertEquals "a", root.getValueAt(0)
        assertEquals "b", root.getValueAt(1)
        assertEquals "c", root.getValueAt(2)

        assertEquals 1.0, child.getValueAt(0)
        assertEquals 2.0, child.getValueAt(1)
        assertEquals 3.0, child.getValueAt(2)

    }
}



class TableTreeMutatorTestNode extends ParameterizationTableTreeNode {
    public TableTreeMutatorTestNode(List parameter, String name) {
        super(parameter);
    }

    public void setValueAt(Object o, int i) {
    }

    public Object getExpandedCellValue(int column) {
        parameter[column].path
    }
}