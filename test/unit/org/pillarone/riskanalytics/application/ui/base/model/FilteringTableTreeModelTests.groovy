package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.event.ITableTreeModelListener
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import models.application.ApplicationModel

class FilteringTableTreeModelTests extends GroovyTestCase {


    void testIsAcceptedNode() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertTrue filteringModel.isAcceptedNode(child21)
        assertTrue filteringModel.isAcceptedNode(child2)
        assertTrue filteringModel.isAcceptedNode(root)
        assertFalse filteringModel.isAcceptedNode(child3)


    }

    void testColumnCount() {
        DefaultTableTreeModel model = new DefaultTableTreeModel(new SimpleTableTreeNode("root"), ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "foo"))
        assertEquals model.columnCount, filteringModel.columnCount
    }

    void testGetValueAt() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child2"))

        assertEquals model.getValueAt(child2, 0), filteringModel.getValueAt(child2, 0)
    }

    void testGetRoot() {
        DefaultTableTreeModel model = new DefaultTableTreeModel(new SimpleTableTreeNode("root"), ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "foo"))
        assertSame(model.root, filteringModel.root)
    }

    void testGetChild() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child22"))

        assertSame child2, filteringModel.getChild(filteringModel.root, 0)
        assertSame child2, filteringModel.getChild(root, 0)
        assertSame child22, filteringModel.getChild(child2, 0)

    }

    void testChildCount() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)

        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "foo"))
        assertEquals 0, filteringModel.getChildCount(filteringModel.root)

        filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child3"))
        assertEquals 1, filteringModel.getChildCount(filteringModel.root)

        filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))
        assertEquals 1, filteringModel.getChildCount(filteringModel.root)
        def acceptedChildNode = filteringModel.getChild(filteringModel.root, 0)
        assertEquals 1, filteringModel.getChildCount(acceptedChildNode)
    }

    void testIsLeaf() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertTrue filteringModel.isLeaf(child21)
        assertFalse filteringModel.isLeaf(child2)
    }

    void testGetIndexOfChild() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertEquals 0, filteringModel.getIndexOfChild(root, child2)
        assertEquals 0, filteringModel.getIndexOfChild(child2, child21)

        assertEquals(-1, filteringModel.getIndexOfChild(child2, child22))

    }

    void testSetFilter() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))
        assertSame child21, filteringModel.getChild(filteringModel.getChild(filteringModel.root, 0), 0)

        filteringModel.filter = new TestTableTreeFilter(criteria: "child3")

        assertSame child3, filteringModel.getChild(filteringModel.root, 0)
    }


    void testTreeSynchronisation() {

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")
        SimpleTableTreeNode child24 = new SimpleTableTreeNode("child24")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> true}] as ITableTreeFilter)

        assertEquals 3, filteringModel.getChildCount(filteringModel.root)
        ITableTreeNode filteredChild2 = filteringModel.getChild(filteringModel.root, 1)
        assertEquals 2, filteringModel.getChildCount(filteredChild2)

        child2.add(child23)
        assertEquals 2, filteringModel.getChildCount(filteredChild2)
        filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        assertEquals "node insertion not synchronized", 3, filteringModel.getChildCount(filteredChild2)

        child2.remove(child22)
        assertEquals 3, filteringModel.getChildCount(filteredChild2)
        filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        assertEquals "node removal not synchonized", 2, filteringModel.getChildCount(filteredChild2)

        child2.remove(child23)
        child2.add(child24)
        assertEquals 2, filteringModel.getChildCount(filteredChild2)
        filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        assertEquals "node removal not synchonized", 2, filteringModel.getChildCount(filteredChild2)

    }

    void testOrderAfterFilter() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")


        root.add(child1)
        root.add(child2)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> true}] as ITableTreeFilter)

        assertEquals 2, filteringModel.getChildCount(filteringModel.root)

        assertEquals "child1", filteringModel.getChild(filteringModel.root, 0).name
        assertEquals "child2", filteringModel.getChild(filteringModel.root, 1).name

        filteringModel.filter = new TestTableTreeFilter(criteria: "child2")

        assertEquals 1, filteringModel.getChildCount(filteringModel.root)

        assertEquals "child2", filteringModel.getChild(filteringModel.root, 0).name

        filteringModel.filter = [acceptNode: {ITableTreeNode node -> true}] as ITableTreeFilter

        assertEquals 2, filteringModel.getChildCount(filteringModel.root)

        assertEquals "child1", filteringModel.getChild(filteringModel.root, 0).name
        assertEquals "child2", filteringModel.getChild(filteringModel.root, 1).name
    }

    void testSynchronisationNotifiesNodesInserted() {

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")
        SimpleTableTreeNode child24 = new SimpleTableTreeNode("child24")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> true}] as ITableTreeFilter)

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child2.add(child23)
            filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        }
        assertNotNull event
        assertSame child2, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals child2.getIndex(child23), event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child23, event.children[0]

    }

    void testSynchronisationNotifiesNodesInsertedWithFilter() {

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")
        SimpleTableTreeNode child24 = new SimpleTableTreeNode("child24")
        SimpleTableTreeNode child31 = new SimpleTableTreeNode("child31")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> node.name.startsWith("child2")}] as ITableTreeFilter)

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child2.add(child23)
            filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        }
        assertNotNull event
        assertSame child2, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals child2.getIndex(child23), event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child23, event.children[0]

        assertNull eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(child31)
            filteringModel.reapplyFilter()
        }


        event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(child24)
            filteringModel.reapplyFilter()
        }
        assertNotNull event
        assertSame child3, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals 0, event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child24, event.children[0]


    }

    void testEventForwardingNodesInsertedWithFilter() {

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")
        SimpleTableTreeNode child24 = new SimpleTableTreeNode("child24")
        SimpleTableTreeNode child31 = new SimpleTableTreeNode("child31")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> node.name.startsWith("child2")}] as ITableTreeFilter)

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child2.add(child23)
            model.nodesWereInserted(new TreePath([root, child2] as Object[]), [2] as int[])
        }
        assertNotNull event
        assertSame child2, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals child2.getIndex(child23), event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child23, event.children[0]

        event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(child31)
            model.nodesWereInserted(new TreePath([root, child3] as Object[]), [0] as int[])
        }
        assertNull event

        event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(child24)
            model.nodesWereInserted(new TreePath([root, child3] as Object[]), [1] as int[])
        }
        assertNotNull event
        assertSame child3, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals 0, event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child24, event.children[0]

    }
    // todo: test event forwarding

    void testSynchronisationNotifiesNodesRemoved() {

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        child2.add(child23)
        root.add(child3)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> true}] as ITableTreeFilter)

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child2.remove(child23)
            filteringModel.synchronizeFilteredTree(child2, filteringModel.nodeMapping[child2])
        }
        assertNotNull event
        assertSame child2, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals 2, event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child23, event.children[0]

    }

    void testEventForwardingNodesRemovedWithFilter() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")
        SimpleTableTreeNode child23 = new SimpleTableTreeNode("child23")
        SimpleTableTreeNode child24 = new SimpleTableTreeNode("child24")
        SimpleTableTreeNode child31 = new SimpleTableTreeNode("child31")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        child2.add(child23)
        root.add(child3)
        child3.add(child31)
        child3.add(child24)

        ParameterizationTableTreeModel model = new ParameterizationTableTreeModel(root: root, columnCount: 1)
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, [acceptNode: {ITableTreeNode node -> node.name.startsWith("child2")}] as ITableTreeFilter)

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child2.remove(child23)
            model.nodesWereRemoved(new TreePath([root, child2] as Object[]), [2] as int[], [child23] as Object[])
        }
        assertNotNull event
        assertSame child2, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals 2, event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child23, event.children[0]

        event = eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child3.remove(child31)
            model.nodesWereRemoved(new TreePath([root, child3] as Object[]), [0] as int[], [child31] as Object[])
        }
        assertNull event


        event = eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child3.remove(child24)
            model.nodesWereRemoved(new TreePath([root, child3] as Object[]), [0] as int[], [child24] as Object[])
        }
        assertNotNull event
        assertSame root, event.treePath.lastPathComponent
        assertEquals 1, event.childIndices.size()
        assertEquals 1, event.childIndices[0]
        assertEquals 1, event.children.size()
        assertSame child3, event.children[0]

    }

    void testForwardNodeChanged() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertNotNull eventForMethod(filteringModel, "tableTreeNodesChanged") {
            model.nodeChanged(child21)
        }

        assertNotNull eventForMethod(filteringModel, "tableTreeNodesChanged") {
            model.nodeChanged(child2)
        }

        assertNull eventForMethod(filteringModel, "tableTreeNodesChanged") {
            model.nodeChanged(child3)
        }
    }

    void testForwardNodeStructureChanged() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertNull eventForMethod(filteringModel, "tableTreeNodesInserted") {
            model.nodeStructureChanged(child21)
        }

        assertNull eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            model.nodeStructureChanged(child21)
        }

        child2.add(new SimpleTableTreeNode("child23"))
        assertNull eventForMethod(filteringModel, "tableTreeNodesInserted") {
            model.nodeStructureChanged(child2)
        }

        assertNull eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            model.nodeStructureChanged(child2)
        }


    }

    void testForwardNodesAdded() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertNotNull "adding a matching node to a matching", eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child21.add(new SimpleTableTreeNode("child21"))
            model.nodesWereInserted(child21, 0)
        }

        assertNull "adding a non matching node to a matching node", eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child2.add(new SimpleTableTreeNode("child211"))
            model.nodesWereInserted(child2, 1)
        }

        assertNull "adding a non matching node to a non matching node", eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(new SimpleTableTreeNode("child31"))
            model.nodesWereInserted(child3, 0)
        }

        TableTreeModelEvent event = eventForMethod(filteringModel, "tableTreeNodesInserted") {
            child3.add(new SimpleTableTreeNode("child21"))
            assertEquals 1, filteringModel.getChildCount(root)
            model.nodesWereInserted(child3, 1)
            assertEquals 2, filteringModel.getChildCount(root)
            assertSame child3, filteringModel.getChild(root, 1)
        }
        assertNotNull event
    }


    void testForwardNodesRemoved() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child1 = new SimpleTableTreeNode("child1")
        SimpleTableTreeNode child2 = new SimpleTableTreeNode("child2")
        SimpleTableTreeNode child3 = new SimpleTableTreeNode("child3")
        SimpleTableTreeNode child31 = new SimpleTableTreeNode("child31")
        SimpleTableTreeNode child21 = new SimpleTableTreeNode("child21")
        SimpleTableTreeNode child22 = new SimpleTableTreeNode("child22")

        root.add(child1)
        root.add(child2)
        child2.add(child21)
        child2.add(child22)
        root.add(child3)
        child3.add(child31)

        DefaultTableTreeModel model = new DefaultTableTreeModel(root, ["1"] as String[])
        FilteringTableTreeModel filteringModel = new FilteringTableTreeModel(model, new TestTableTreeFilter(criteria: "child21"))

        assertNotNull eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child2.children.remove(child21)
            model.nodesWereRemoved(child2, [0] as int[], [child21] as Object[])
        }

        assertNull eventForMethod(filteringModel, "tableTreeNodesRemoved") {
            child3.children.remove(child31)
            model.nodesWereRemoved(child3, [0] as int[], [child31] as Object[])
        }

    }

    void testFilteringParameterizationNodes() {
        DefaultMutableTableTreeNode root = new DefaultMutableTableTreeNode("root")
        DefaultMutableTableTreeNode modelNode = new DefaultMutableTableTreeNode("model")
        DefaultMutableTableTreeNode parameterizationsNode = new ItemGroupNode("Parameterization", Parameterization)
        DefaultMutableTableTreeNode resultConfigurationsNode = new ItemGroupNode("ResultTemplates", ResultConfiguration)
        DefaultMutableTableTreeNode simulationsNode = new ItemGroupNode("Results", Simulation)
        modelNode.add(parameterizationsNode)
        modelNode.add(resultConfigurationsNode)
        modelNode.add(simulationsNode)
        root.add(modelNode)



        Parameterization parameterization1 = new Parameterization("param1")



        ModellingInformationTableTreeModel model = new ModellingInformationTableTreeModel(null)
        FilteringTableTreeModel filter = new MultiFilteringTableTreeModel(model)

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel(filter)
        Model itemModel = new ApplicationModel()
        ParameterizationUIItem parameterizationUIItem = new ParameterizationUIItem(mainModel, itemModel, parameterization1)
        ParameterizationNode parameterizationNode1 = new ParameterizationNode(parameterizationUIItem)
        parameterizationsNode.add parameterizationNode1

        assertTrue filter.isAcceptedNode(modelNode)
        assertTrue filter.isAcceptedNode(parameterizationsNode)
        assertTrue filter.isAcceptedNode(resultConfigurationsNode)
        assertTrue filter.isAcceptedNode(simulationsNode)
        assertTrue filter.isAcceptedNode(parameterizationNode1)

        filter.addFilter(new ModellingItemNodeFilter(["test1", "test2"], 0, false) )

        parameterizationNode1.values[0] = "test2"
        assertTrue filter.isAcceptedNode(parameterizationNode1)

        parameterizationNode1.values[0] = "test3"
        assertFalse filter.isAcceptedNode(parameterizationNode1)
    }



    def eventForMethod(ITableTreeModel filteringModel, String method, Closure yield) {
        def e = null
        ITableTreeModelListener listener = [(method): {event -> e = event}] as ITableTreeModelListener
        filteringModel.addTableTreeModelListener(listener)
        yield()
        return e
    }

}

class TestTableTreeModelListener implements ITableTreeModelListener {

    TableTreeModelEvent event

    public void tableTreeStructureChanged(TableTreeModelEvent event) {
        this.@event = event
    }

    public void tableTreeNodeStructureChanged(TableTreeModelEvent event) {
        this.@event = event
    }

    public void tableTreeNodesInserted(TableTreeModelEvent event) {
        this.@event = event
    }

    public void tableTreeNodesRemoved(TableTreeModelEvent event) {
        this.@event = event
    }

    public void tableTreeNodesChanged(TableTreeModelEvent event) {
        this.@event = event
    }

}


class TestTableTreeFilter implements ITableTreeFilter {
    def criteria

    public boolean acceptNode(ITableTreeNode node) {
        node.name == criteria
    }
}