package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.tree.TreePath


abstract class AbstractParametrizedTableTreeModel extends AbstractCommentableItemTableTreeModel {

    Boolean readOnly = false
    ITableTreeNode root

    private List<TableTreeValueChangedListener> valueChangedListeners = []
    private Map nonValidValues = [:]


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
        if (notifyValueChanged)
            notifyNodeValueChanged(node.parent, column)

    }

    /**
     * set a value without notify a TableTreeValueChangedListeners
     */
    public boolean updateNodeValue(Object value, Object node, int column) {
        boolean notifyChanged = true
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
                node.setValueAt(value, column)
                notifyChanged = adjustTreeStructure(node, column, value)
                notifyValueChanged = true
            }

        }
        if (notifyChanged) {
            nodeChanged getPath(node), column
        }
        return notifyValueChanged
    }

    private boolean adjustTreeStructure(ITableTreeNode node, int column, Object value) {
        return true
    }

    private boolean adjustTreeStructure(ParameterizationClassifierTableTreeNode node, int column, Object value) {
        ParameterObjectParameterTableTreeNode parent = node.parent
        ParameterObjectParameterTableTreeNode newNode = ParameterizationNodeFactory.getNode(node.parameter, simulationModel)

        List nodesToRemove = []
        parent.children.each {
            nodesToRemove << it
        }
        //remove comments
        nodesToRemove.each {
            if (it.comments) {
                commentsToBeDeleted.addAll(it.comments as List)
            }
        }

        List removedIndices = []
        nodesToRemove.each {
            removedIndices << parent.remove(it)
        }

        nodesWereRemoved(getPath(parent), removedIndices as int[], removedIndices as Object[])

        List addedIndices = []
        newNode.children.each {
            addedIndices << parent.add(it)
            def comments = builder?.item?.comments?.findAll {Comment c ->
                c.path == it.path
            }
            comments.each {Comment c ->
                if (commentsToBeDeleted.contains(c))
                    commentsToBeDeleted.remove(c)
            }
            it.comments = comments
        }
        nodesWereInserted(getPath(parent), addedIndices as int[])
        changedComments()
        return false
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

}
