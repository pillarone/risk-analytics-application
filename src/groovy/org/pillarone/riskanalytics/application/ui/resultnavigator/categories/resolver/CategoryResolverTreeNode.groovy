package org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver

import com.ulcjava.base.application.tree.IMutableTreeNode
import com.ulcjava.base.application.tree.ITreeNode
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver

/**
 * Tree node used for constructing the tree with the category resolvers.
 * Note that a category defined in the CategoryMapping associates a category descriptor with
 * a recipe to assign a value in that category by using a ICategoryResolver.
 *
 * @author martin.melchior
 */
class CategoryResolverTreeNode implements IMutableTreeNode {

    ICategoryResolver resolver = null

    private List<CategoryResolverTreeNode> children = []
    private CategoryResolverTreeNode parent = null

    CategoryResolverTreeNode(ICategoryResolver matcher, CategoryResolverTreeNode parent) {
        this.resolver = matcher
        this.parent = parent
    }

    void add(IMutableTreeNode iMutableTreeNode) {
        if (iMutableTreeNode instanceof CategoryResolverTreeNode) {
            children.add((CategoryResolverTreeNode)iMutableTreeNode)
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
            resolver = (ICategoryResolver) o
        }
    }

    void setParent(IMutableTreeNode iMutableTreeNode) {
        if (iMutableTreeNode instanceof CategoryResolverTreeNode) {
            this.parent = (CategoryResolverTreeNode) iMutableTreeNode
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
        return resolver.getName()
    }
}
