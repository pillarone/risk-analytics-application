package org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator

import com.ulcjava.base.application.tabletree.ITableTreeNode

class PreviewNode implements ITableTreeNode {
    PreviewNode parent
    List<PreviewNode> children = new LinkedList<PreviewNode>()
    Object[] data

    PreviewNode (Object[] data) {
        this.data = data
    }

    ITableTreeNode getChildAt(int i) {
        return children[i]
    }

    int getChildCount() {
        return children.size()
    }

    ITableTreeNode getParent() {
        return parent
    }

    int getIndex(ITableTreeNode iTableTreeNode) {
        return children.indexOf(iTableTreeNode)
    }

    Object getValueAt(int i) {
        return this.data[i]
    }

    boolean isLeaf() {
        return childCount == 0
    }

    int addChild (PreviewNode child) {
        children.add (child)
        child.parent = this
        return childCount - 1
    }

    void removeAllChildren () {
        children.clear()
    }

    String getPathString () {
        if (parent == null)
            return ""

        return this.getValueAt(0) + "/" + parent.getPathString()
    }
}
