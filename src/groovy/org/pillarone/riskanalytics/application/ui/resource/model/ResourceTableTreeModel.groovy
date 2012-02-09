package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractCommentableItemTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode


class ResourceTableTreeModel extends AbstractCommentableItemTableTreeModel {

    private ResourceTreeBuilder builder
    private Map nonValidValues = [:]


    ITableTreeNode root

    ResourceTableTreeModel(ResourceTreeBuilder builder) {
        this.builder = builder
        root = builder.root
    }

    int getColumnCount() {
        return 2
    }

    Object getValueAt(Object node, int i) {
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
}
