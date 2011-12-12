package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.tree.ITreeCellRenderer
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import com.ulcjava.base.application.tree.DefaultTreeModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.Matcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AndMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.OrMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ConditionalAssignment

/**
 * @author martin.melchior
 */
class MatcherTree extends ULCTree {

    private DefaultTreeModel fTreeModel;

    MatcherTree(ICategoryMatcher matcher) {
        super()
        fTreeModel = new DefaultTreeModel(createTreeNode(matcher));
        this.setModel(fTreeModel);
        this.setScrollsOnExpand(true);
    }

    private DefaultMutableTreeNode createTreeNode(ICategoryMatcher matcher) {
        switch(matcher.matcherType()) {
            case Matcher.AND:
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(matcher.matcherType().toString());
                for (ICategoryMatcher child : ((AndMatcher) matcher).children) {
                    DefaultMutableTreeNode childNode = createTreeNode(child)
                    node.add(childNode)
                }
                return node
            case Matcher.OR:
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(matcher.matcherType().toString());
                for (ICategoryMatcher child : ((OrMatcher) matcher).children) {
                    DefaultMutableTreeNode childNode = createTreeNode(child)
                    node.add(childNode)
                }
                return node
            case Matcher.BY_CONDITION:
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(matcher.matcherType().toString());
                DefaultMutableTreeNode childNode = createTreeNode(((ConditionalAssignment) matcher).condition)
                node.add(childNode)
                return node
            default:
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(matcher.matcherType().toString(), true);
                return node
        }
    }


    ITreeCellRenderer getRenderer(ICategoryMatcher matcher) {
        return new CustomMatcherTreeCellRenderer(matcher.matcherType())


        /*switch(matcher.matcherType()) {
            case Matcher.BY_SINGLE_LIST_VALUE:
                return new CustomMatcherTreeCellRenderer(Matcher.BY_SINGLE_LIST_VALUE)
            case Matcher.BY_REGEX:
            case Matcher.BY_ENCLOSING:
            case Matcher.BY_ENDING:
            case Matcher.BY_CONDITION:
            case Matcher.BY_CROSS_SECTION:
            case Matcher.AND:
            case Matcher.OR:
            default: return null
        }*/
    }

    private static class CustomMatcherTreeCellRenderer extends ULCLabel implements ITreeCellRenderer {

        Matcher matcher

        CustomMatcherTreeCellRenderer(Matcher matcher) {
            this.matcher = matcher
        }

        IRendererComponent getTreeCellRendererComponent(ULCTree ulcTree, Object o, boolean b, boolean b1, boolean b2, boolean b3) {
            this.setName(matcher.toString())
            return this
        }
    }
}
