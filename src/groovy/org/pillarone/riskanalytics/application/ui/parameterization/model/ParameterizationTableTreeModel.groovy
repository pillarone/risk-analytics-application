package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.parameter.Parameter

class ParameterizationTableTreeModel extends AbstractTableTreeModel {

    ParameterizationTreeBuilder builder
    Model simulationModel
    ITableTreeNode root
    int columnCount
    private List valueChangedListeners = []
    Boolean readOnly = false
    Map nonValidValues = [:]



    public ParameterizationTableTreeModel() {
    }

    public ParameterizationTableTreeModel(ParameterizationTreeBuilder builder) {
        this.builder = builder
        this.root = builder.root
        this.columnCount = builder.periodCount + 1
    }

    void addValueChangedListener(TableTreeValueChangedListener listener) {
        valueChangedListeners.add(listener)
    }

    void removeValueChangedListener(TableTreeValueChangedListener listener) {
        valueChangedListeners.remove(listener)
    }

    void notifyNodeValueChanged(Object node, int column) {
        valueChangedListeners.each {
            it.valueChanged(node, column)
        }
    }

    boolean isCellEditable(Object node, int columnIndex) {
        readOnly ? false : node.isCellEditable(columnIndex)
    }

    public int getColumnCount() {
        return columnCount
    }

    public Object getValueAt(Object node, int i) {
        def value
        if (nonValidValues[[node, i]] != null) {
            value = nonValidValues[[node, i]]
        } else {
            value = node.getValueAt(i)
        }
        return value
    }

    public Object getRoot() {
        return root
    }

    public Object getChild(Object parent, int index) {
        return parent.getChildAt(index)
    }

    public int getChildCount(Object node) {
        return node.childCount
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0
    }

    public int getIndexOfChild(Object parent, Object child) {
        return parent.getIndex(child)
    }

    protected Object getI18NValue(ParameterizationClassifierTableTreeNode node, Object value) {
        return node.getKeyForValue(value)
    }


    public void setValueAt(Object value, Object node, int column) {

        boolean notifyChanged = true
        if (nonValidValues.containsKey([node, column])) {
            nonValidValues.remove([node, column])
        }

        if (value == null) {
            nonValidValues[[node, column]] = ""
        } else {

            def oldValue = node.getValueAt(column)

            // This check is because the use of a ErrorManager in the Editor causes the wrong input to be send to the ULC-side.
            // The wrong input will be sent as String instead of a Number, so we check the type of the old value against the new one
            if (value instanceof String && !(oldValue instanceof String)) {
                nonValidValues[[node, column]] = value
            } else if (value != oldValue && !readOnly) {
                def parent = node.parent
                node.setValueAt(value, column)
                notifyChanged = adjustTreeStructure(node, column, value)
                notifyNodeValueChanged(parent, column)
            }

        }
        if (notifyChanged) {
            nodeChanged getPath(node), column
        }
    }

    private boolean adjustTreeStructure(ITableTreeNode node, int column, Object value) {
        return true
    }

    private boolean adjustTreeStructure(ParameterizationClassifierTableTreeNode node, int column, Object value) {
        ParameterObjectParameterTableTreeNode parent = node.parent
        //TODO (msp) keep old values
        ParameterObjectParameterTableTreeNode newNode = ParameterizationNodeFactory.getNode(node.parameter, simulationModel)

        List nodesToRemove = []
        parent.children.each {
            nodesToRemove << it
        }

        List removedIndices = []
        nodesToRemove.each {
            removedIndices << parent.remove(it)
        }

        nodesWereRemoved(getPath(parent), removedIndices as int[], removedIndices as Object[])

        List addedIndices = []
        newNode.children.each {
            addedIndices << parent.add(it)
        }
        nodesWereInserted(getPath(parent), addedIndices as int[])
        return false
    }

    public void addComponentNode(ComponentTableTreeNode parent, Component component) {
        parent.component.addSubComponent(component)
        ComponentTableTreeNode node = builder.createNewComponentNode(parent, component)
        nodesWereInserted(getPath(parent), parent.getIndex(node))
        notifyNodeValueChanged(node, -1)
        notifyComboBoxNodesComponentAdded(component)
    }

    private void notifyComboBoxNodesComponentAdded(Component newComponent) {
        List nodes = []
        findNodes(root, nodes)
        for (ConstrainedStringParameterizationTableTreeNode node in nodes) {
            node.addComponent(newComponent)
            nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }
    }

    private void notifyComboBoxNodesComponentRemoved(Component newComponent) {
        List nodes = []
        findNodes(root, nodes)
        for (ConstrainedStringParameterizationTableTreeNode node in nodes) {
            node.removeComponent(newComponent)
            nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }
    }

    private void findNodes(ITableTreeNode current, List nodes) {
        if (current instanceof ConstrainedStringParameterizationTableTreeNode) {
            nodes << current
        }
        for (int i = 0; i < current.childCount; i++) {
            findNodes(current.getChildAt(i), nodes)
        }
    }

    public void removeComponentNode(ComponentTableTreeNode componentNode) {
        builder.removeParameterFromNodes(componentNode)
        int childIndex = componentNode.parent.getIndex(componentNode)
        ComponentTableTreeNode parent = componentNode.parent
        parent.component.removeSubComponent(componentNode.component)
        parent.remove(componentNode)
        nodesWereRemoved(getPath(parent), [childIndex] as int[], [componentNode] as Object[])
        notifyNodeValueChanged(componentNode, -1)
        notifyComboBoxNodesComponentRemoved(componentNode.component)
    }

    private TreePath getPath(ITableTreeNode node) {
        def path = []
        ITableTreeNode nodeInPath = node
        while (nodeInPath != null) {
            path << nodeInPath
            nodeInPath = nodeInPath.parent
        }
        return new TreePath(path.reverse() as Object[])
    }

    public void expansionChanged(TreePath path, boolean expanded) {
        path.lastPathComponent.expanded = expanded
        nodeChanged(path)
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Name"
        }

        if (builder != null) {
            return builder.item.getPeriodLabel(column - 1)
        }
        return null
    }


}

interface TableTreeValueChangedListener {
    void valueChanged(Object node, int column)
}