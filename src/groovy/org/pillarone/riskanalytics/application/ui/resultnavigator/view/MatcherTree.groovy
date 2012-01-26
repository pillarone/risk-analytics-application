package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.tree.DefaultTreeModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.MatcherTreeNode
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AndResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ConditionalAssignmentResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.OrResolver

/**
 * @author martin.melchior
 */
class MatcherTree extends ULCTree {

    private DefaultTreeModel fTreeModel;

    MatcherTree(ICategoryResolver matcher) {
        super()
        fTreeModel = new DefaultTreeModel(createTreeNode(matcher, null));
        this.setModel(fTreeModel);
        this.setScrollsOnExpand(true);
    }

    private MatcherTreeNode createTreeNode(ICategoryResolver matcher, MatcherTreeNode parent) {
        MatcherTreeNode node = new MatcherTreeNode(matcher, parent);
        switch(matcher) {
            case AndResolver:
                for (ICategoryResolver child : ((AndResolver) matcher).children) {
                    MatcherTreeNode childNode = createTreeNode(child, parent)
                    node.add(childNode)
                }
                return node
            case OrResolver:
                for (ICategoryResolver child : ((OrResolver) matcher).children) {
                    MatcherTreeNode childNode = createTreeNode(child, parent)
                    node.add(childNode)
                }
                return node
            case ConditionalAssignmentResolver:
                MatcherTreeNode childNode = createTreeNode(((ConditionalAssignmentResolver) matcher).condition, parent)
                node.add(childNode)
                return node
            default:
                return node
        }
    }
}
