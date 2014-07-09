package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

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
        ParameterHolder holder1 = ParameterHolderFactory.getHolder("path1", 0, "value")
        ParameterHolder holder2 = ParameterHolderFactory.getHolder("path1", 1, "value")
        ParameterHolder holder3 = ParameterHolderFactory.getHolder("path1", 2, "value")
        Parameterization parameterization = new Parameterization("")
        parameterization.addParameter(holder1)
        parameterization.addParameter(holder2)
        parameterization.addParameter(holder3)

        ParameterizationTableTreeNode child = new TableTreeMutatorTestNode(holder1.path, parameterization, "path")
        root.add(child)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["col1", "col2", "col3"] as String[])

        TableTreeMutator mutator = new TableTreeMutator(model)
        shouldFail(Exception, { mutator.applyChanges([root, child], [["A", "B", "C"], [1.1, 2.2, 3.3]]) })


        assertEquals "a", root.getValueAt(0)
        assertEquals "b", root.getValueAt(1)
        assertEquals "c", root.getValueAt(2)

        assertEquals ComponentUtils.getNormalizedName("path1"), child.getValueAt(0)
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
        mutator.metaClass.showReadOnlyAlert = {->}
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

    String name

    public TableTreeMutatorTestNode(String path, ParametrizedItem item, String name) {
        super(path, item);
        this.name = name
    }

    public void setValueAt(Object o, int i) {
    }

    public Object doGetExpandedCellValue(int column) {
        "${name}${column + 1}"
    }
}