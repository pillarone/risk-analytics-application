package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.tree.DefaultTreeModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AndMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.OrMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ConditionalAssignment
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.MatcherTreeNode

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
            case AndMatcher:
                for (ICategoryResolver child : ((AndMatcher) matcher).children) {
                    MatcherTreeNode childNode = createTreeNode(child, parent)
                    node.add(childNode)
                }
                return node
            case OrMatcher:
                for (ICategoryResolver child : ((OrMatcher) matcher).children) {
                    MatcherTreeNode childNode = createTreeNode(child, parent)
                    node.add(childNode)
                }
                return node
            case ConditionalAssignment:
                MatcherTreeNode childNode = createTreeNode(((ConditionalAssignment) matcher).condition, parent)
                node.add(childNode)
                return node
            default:
                return node
        }
    }
}
