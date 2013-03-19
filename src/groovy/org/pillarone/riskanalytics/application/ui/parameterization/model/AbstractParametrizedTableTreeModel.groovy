package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.core.model.Model
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.core.components.Component


abstract class AbstractParametrizedTableTreeModel extends AbstractCommentableItemTableTreeModel {

    private static Log LOG = LogFactory.getLog(AbstractParametrizedTableTreeModel)

    Boolean readOnly = false
    ITableTreeNode root

    private List<TableTreeValueChangedListener> valueChangedListeners = []
    private Map nonValidValues = [:]
    protected HashSet<ITableTreeNode> toBeInserted = new HashSet<ITableTreeNode>()


    AbstractParametrizedTableTreeModel(ITableTreeNode root) {
        this.root = root
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
        if (readOnly) {
            return false
        }
        List<String> allEditablePaths = getAllEditablePaths()
        if (allEditablePaths.size() == 0 || allEditablePaths.any { node.path.startsWith(it)}) {
            return node.isCellEditable(columnIndex)
        } else {
            return false
        }
    }

    abstract protected List<String> getAllEditablePaths()

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

    public void setValueAt(Object value, Object node, int column) {
        boolean notifyValueChanged = updateNodeValue(value, node, column)
        if (notifyValueChanged) {
            notifyNodeValueChanged(node.parent, column)
        }
    }

    /**
     * set a value without notify a TableTreeValueChangedListeners
     */
    public boolean updateNodeValue(Object value, Object node, int column) {
        boolean notifyValueChanged = false

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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Setting value ${value} at ${node.path}")
                }
                node.setValueAt(value, column)
                notifyValueChanged = true
            }

        }
        return notifyValueChanged
    }

    protected abstract Model getSimulationModel()

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

    void componentAdded(String path, Component component) {
        //dynamic components supported in parameterization only
    }

    void componentRemoved(String path) {
        //dynamic components supported in parameterization only
    }

    void parameterValuesChanged(List<String> paths) {
        for (SimpleTableTreeNode node in paths.collect { findNode(it.split(":")) }) {
            if (node != null && !isBeingInserted(node)) {
                nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
            }

        }
    }


    protected isBeingInserted(SimpleTableTreeNode node) {
        SimpleTableTreeNode current = node
        while (current != null) {
            if (toBeInserted.contains(current)) {
                return true
            }
            current = current.parent
        }
        return false
    }

    void classifierChanged(String path) {
        ParameterObjectParameterTableTreeNode parameterObjectNode = findNode(path.split(":"))

        ITableTreeNode parent = parameterObjectNode.parent
        toBeInserted.add(parent)
        try {
            ParameterObjectParameterTableTreeNode newParameterObjectNode = ParameterizationNodeFactory.getNode(parameterObjectNode.parameterPath, parameterObjectNode.parametrizedItem, getSimulationModel())

            int[] removedIndices = new int[parameterObjectNode.childCount]
            Object[] removedChildren = new Object[parameterObjectNode.childCount]
            int children = parameterObjectNode.childCount
            for (int i = 0; i < children; i++) {
                removedIndices[i] = i
                removedChildren[i] = parameterObjectNode.getChildAt(i)
            }
            for (ITableTreeNode node in removedChildren) {
                parameterObjectNode.remove(node)
            }
            nodesWereRemoved(getPath(parameterObjectNode), removedIndices, removedChildren)

            int[] addedIndices = new int[newParameterObjectNode.childCount]
            for (int i = 0; i < newParameterObjectNode.childCount; i++) {
                parameterObjectNode.add(newParameterObjectNode.getChildAt(i))
                addedIndices[i] = i
            }
            nodesWereInserted(getPath(parameterObjectNode), addedIndices)
        } finally {
            toBeInserted.remove(parent)
        }
    }

    protected ComponentTableTreeNode findComponentNode(String[] pathComponents) {
        SimpleTableTreeNode node = findNode(pathComponents)
        if (node instanceof ComponentTableTreeNode) {
            return node
        }
        throw new IllegalArgumentException("No component found at ${pathComponents.join(":")}")
    }

    protected SimpleTableTreeNode findNode(String[] pathComponents) {
        SimpleTableTreeNode current = getRoot() as SimpleTableTreeNode
        for (String currentElement in pathComponents) {
            if(isBeingInserted(current)) {
                return null
            }
            current = current.getChildByName(currentElement)
            if (current == null) {
                throw new IllegalArgumentException("Node with name $currentElement not found in path ${pathComponents.join(":")}")
            }
        }
        return current
    }

}
