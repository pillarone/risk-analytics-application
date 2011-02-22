package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.event.ITableTreeModelListener
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath

class FilteringTableTreeModel extends AbstractTableTreeModel implements ITableTreeModelListener {

    ITableTreeModel model
    ITableTreeFilter filter
    FilterTableTreeNode filteredRoot
    def nodeMapping = [:]

    public FilteringTableTreeModel(ITableTreeModel model, ITableTreeFilter filter) {
        this.@model = model
        this.@filter = filter
        filteredRoot = new FilterTableTreeNode(originalNode: model.root)
        applyFilter()
        this.@model.addTableTreeModelListener this
    }

    public void setFilter(ITableTreeFilter newFilter) {
        this.@filter = newFilter
        applyFilter()
    }

    public void applyFilter() {
        synchronizeFilteredTree(model.root, filteredRoot)
    }

    protected void reapplyFilter() {
        synchronizeFilteredTree(model.root, filteredRoot)
    }

    private void synchronizeTreePath(TreePath path) {
        def node = findValidSynchronizationStart(path)

        synchronizeFilteredTree(node, nodeMapping[node])
    }

    private def findValidSynchronizationStart(TreePath path) {
        Object node = path.lastPathComponent
        while (!(nodeMapping[node] && isAcceptedNode(node)) && node.parent != null) {
            node = node.parent
        }
        return node
    }



    protected void synchronizeFilteredTree(ITableTreeNode node, FilterTableTreeNode filteredNode) {
        nodeMapping[node] = filteredNode
        node.childCount.times {childIndex ->

            def childNode = node.getChildAt(childIndex)
            FilterTableTreeNode filteredChildNode = filteredNode.childNodes.find {it.originalNode == childNode}
            boolean nodeCurrentlyActive = filteredNode.activeIndices.contains(filteredNode.childNodes.indexOf(filteredChildNode))
            if (isAcceptedNode(childNode)) {
                if (!filteredChildNode) {
                    filteredChildNode = new FilterTableTreeNode(parent: filteredNode, originalChildIndex: childIndex, originalNode: childNode)

                    filteredNode.childNodes << filteredChildNode
                    filteredNode.activeIndices << filteredNode.childNodes.indexOf(filteredChildNode)
                    nodeMapping[childNode] = filteredChildNode
                    nodesWereInserted(new TreePath(getPathToRoot(node) as Object[]), [getIndexOfChild(node, childNode)] as int[])
                } else if (!nodeCurrentlyActive) {
                    filteredNode.activeIndices << filteredNode.childNodes.indexOf(filteredChildNode)
                    filteredNode.activeIndices = filteredNode.activeIndices.sort()
                    nodesWereInserted(new TreePath(getPathToRoot(node) as Object[]), [getIndexOfChild(node, childNode)] as int[])
                }
                synchronizeFilteredTree(childNode, filteredChildNode)
            } else {
                if (filteredChildNode && nodeCurrentlyActive) {
                    removeFilteredChildNodeIndex(filteredChildNode)
                }
            }
        }

        def iterator = filteredNode.childNodes.iterator()
        while (iterator.hasNext()) {
            def filteredChildNode = iterator.next()
            if (node.getIndex(filteredChildNode.originalNode) < 0) {
                removeFilteredChildNode(filteredChildNode, iterator)
            }
        }
    }

    private def removeFilteredChildNodeIndex(FilterTableTreeNode filteredChildNode) {
        def filteredNode = filteredChildNode.parent
        int removedIndex = filteredNode.childNodes.indexOf(filteredChildNode)
        def activeIndicesIndex = filteredNode.activeIndices.indexOf(removedIndex)

        filteredNode.activeIndices.remove(activeIndicesIndex)
        nodesWereRemoved(new TreePath(getPathToRoot(filteredNode.originalNode) as Object[]), [activeIndicesIndex] as int[], [filteredChildNode.originalNode] as Object[])
    }

    private def removeFilteredChildNode(FilterTableTreeNode filteredChildNode, Iterator iterator) {
        def filteredNode = filteredChildNode.parent
        int removedIndex = filteredNode.childNodes.indexOf(filteredChildNode)
        def activeIndicesIndex = filteredNode.activeIndices.indexOf(removedIndex)
        if (activeIndicesIndex >= 0) {
            filteredNode.activeIndices.remove(activeIndicesIndex)
            nodesWereRemoved(new TreePath(getPathToRoot(filteredNode.originalNode) as Object[]), [activeIndicesIndex] as int[], [filteredChildNode.originalNode] as Object[])
        }
        iterator.remove()
        def indicesIterator = filteredNode.activeIndices.iterator()
        def newIndices = []
        while (indicesIterator.hasNext()) {
            Integer i = indicesIterator.next()
            if (i > removedIndex) {
                indicesIterator.remove()
                newIndices << --i
            }
        }
        for (int i in newIndices) {
            filteredNode.activeIndices << i
        }
        filteredNode.activeIndices = filteredNode.activeIndices.sort()

    }

    /**
     * Returns the path to the root.
     *
     * @param node the node to get the path for
     * @return the path to the root
     */
    public static ITableTreeNode[] getPathToRoot(ITableTreeNode node) {
        List result = new ArrayList();
        result.add(node);
        while (node.getParent() != null) {
            node = node.getParent();
            result.add(node);
        }

        Collections.reverse(result);
        return (ITableTreeNode[]) result.toArray(new ITableTreeNode[result.size()]);
    }


    public int getColumnCount() {
        return model.columnCount;
    }

    public void setColumnCount(int newColumnCount) {
        model.columnCount = newColumnCount
    }

    public String getColumnName(int column) {
        model.getColumnName(column)
    }

    public Object getValueAt(Object node, int column) {
        return model.getValueAt(node, column)
    }

    public Object getRoot() {
        return model.root;
    }

    public Object getChild(Object parent, int index) {
        def filteredNode = nodeMapping[parent]
        int i = filteredNode.activeIndices[index]

        return filteredNode.childNodes[i].originalNode
    }

    public int getChildCount(Object parent) {
        return nodeMapping[parent].activeIndices.size();
    }

    public boolean isLeaf(Object node) {
        return node.childCount == 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        def filteredNode = nodeMapping[parent]
        int originalIndex = filteredNode.childNodes.indexOf(nodeMapping[child])
        return filteredNode.activeIndices.indexOf(originalIndex)
    }

    public boolean isCellEditable(Object node, int columnIndex) {
        return model.isCellEditable(node, columnIndex);
    }

    public void setValueAt(Object value, Object node, int column) {
        model.setValueAt(value, node, column);
    }

    // event forwarding by reapplying the filter

    public void tableTreeStructureChanged(TableTreeModelEvent event) {
        tableTreeNodeStructureChanged(event)
    }

    public void tableTreeNodeStructureChanged(TableTreeModelEvent event) {
        synchronizeTreePath(event.treePath)
    }

    public void tableTreeNodesInserted(TableTreeModelEvent event) {
        synchronizeTreePath(event.treePath)
    }

    public void tableTreeNodesRemoved(TableTreeModelEvent event) {
        synchronizeTreePath(event.treePath)
    }

    public void tableTreeNodesChanged(TableTreeModelEvent event) {
        List childIndices = []
        event.children.each {
            int childIndex = getIndexOfChild(event.treePath.lastPathComponent, it)
            if (childIndex >= 0) {
                childIndices << childIndex
            }
        }
        if (!childIndices.empty) {
            nodesChanged(event.treePath, childIndices as int[])
        }
    }

    protected TreePath extractPath(TableTreeModelEvent event) {
        def pathElements = event.getTreePath().getPath().toList()
        def elements = new ArrayList(pathElements)
        if (event.children) {
            elements.addAll(event.children.toList())
        }

        TreePath path = new TreePath(elements as Object[])
        return path
    }

    protected boolean isAcceptedNode(ITableTreeNode node) {
        boolean nodeAccepted = filter.acceptNode(node)
        if (!nodeAccepted) {
            node.childCount.times {
                nodeAccepted |= isAcceptedNode((node.getChildAt(it)))
            }
        }
        return nodeAccepted
    }

    Object methodMissing(String name, Object args) {
        model.getMetaClass().invokeMethod(model, name, args)
    }

    Object propertyMissing(String name) {
        model.getMetaClass().getProperty(model, name)
    }

    void propertyMissing(String name, Object args) {
        model.getMetaClass().setProperty(model, name, args)
    }


}

class FilterTableTreeNode {

    FilterTableTreeNode parent
    List childNodes
    List activeIndices
    int originalChildIndex
    ITableTreeNode originalNode

    public FilterTableTreeNode() {
        childNodes = []
        activeIndices = []
    }

}