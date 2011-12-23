package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import com.ulcjava.base.application.tree.IMutableTreeNode
import com.ulcjava.base.application.tree.ITreeNode
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver

/**
 * @author martin.melchior
 */
class MatcherTreeNode implements IMutableTreeNode {
    ICategoryResolver matcher = null
    List<MatcherTreeNode> children = []
    MatcherTreeNode parent = null

    MatcherTreeNode(ICategoryResolver matcher, MatcherTreeNode parent) {
        this.matcher = matcher
        this.parent = parent
    }

    void add(IMutableTreeNode iMutableTreeNode) {
        if (iMutableTreeNode instanceof MatcherTreeNode) {
            children.add((MatcherTreeNode)iMutableTreeNode)
        }
    }

    void insert(IMutableTreeNode iMutableTreeNode, int i) {
        add(iMutableTreeNode)
    }

    void remove(int i) {
        children.remove i
    }

    void setUserObject(Object o) {
        if (o instanceof ICategoryResolver) {
            matcher = (ICategoryResolver) o
        }
    }

    void setParent(IMutableTreeNode iMutableTreeNode) {
        if (iMutableTreeNode instanceof MatcherTreeNode) {
            this.parent = (MatcherTreeNode) iMutableTreeNode
        }
    }

    ITreeNode getChildAt(int i) {
        return children[i]
    }

    int getChildCount() {
        return children.size()
    }

    ITreeNode getParent() {
        return parent
    }

    int getIndex(ITreeNode iTreeNode) {
        return children.indexOf(iTreeNode)
    }

    boolean isLeaf() {
        return children.size()==0
    }

    String toString() {
        return matcher.getName()
    }
}
