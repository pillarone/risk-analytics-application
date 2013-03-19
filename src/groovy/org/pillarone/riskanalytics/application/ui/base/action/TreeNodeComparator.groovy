package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode

class TreeNodeComparator implements Comparator<ITableTreeNode> {

    private ITableTreeNode root

    TreeNodeComparator(ITableTreeNode root) {
        this.root = root
    }

    int compare(ITableTreeNode node1, ITableTreeNode node2) {
        ITableTreeNode[] path1 = DefaultTableTreeModel.getPathToRoot(node1)
        ITableTreeNode[] path2 = DefaultTableTreeModel.getPathToRoot(node2)

        ITableTreeNode commonParent = path1[0]
        int foundIndex = 0
        for (int i = 1; i < (path1.length - 1) && i < (path2.length - 1); i++) {
            if (path1[i] == path2[i]) {
                commonParent = path1[i]
                foundIndex = i
            }
        }

        return commonParent.getIndex(path1[foundIndex + 1]) < commonParent.getIndex(path2[foundIndex + 1]) ? -1 : 1
    }

}
