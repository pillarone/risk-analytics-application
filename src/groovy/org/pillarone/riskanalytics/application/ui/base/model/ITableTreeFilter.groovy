package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode

interface ITableTreeFilter {

    boolean acceptNode(ITableTreeNode node)

}

class NodeNameFilter implements ITableTreeFilter {

    String nodeName

    public NodeNameFilter(nodeName) {
        this.nodeName = nodeName;
    }

    public boolean acceptNode(ITableTreeNode node) {
        node ? (nodeName != null ? internalAcceptNode(node) : true) : false
    }

    boolean internalAcceptNode(ITableTreeNode node) {
        return acceptNode(node.parent)
    }

    boolean internalAcceptNode(ComponentTableTreeNode node) {
        return nodeName == node.displayName || acceptNode(node.parent)
    }

    boolean internalAcceptNode(DynamicComposedComponentTableTreeNode node) {
        return nodeName == node.displayName || acceptNode(node.parent)
    }

    boolean internalAcceptNode(ResultStructureTableTreeNode node) {
        return nodeName == node.displayName || acceptNode(node.parent)
    }

}

