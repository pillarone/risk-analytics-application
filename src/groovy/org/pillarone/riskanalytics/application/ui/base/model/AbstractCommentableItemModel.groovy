package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.comment.view.ChangedCommentListener
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.comment.view.TabbedPaneChangeListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractCommentableItemModel extends AbstractModellingModel {

    List<ChangedCommentListener> changedCommentListeners
    List<NavigationListener> navigationListeners
    List<TabbedPaneChangeListener> tabbedPaneChangeListeners

    public AbstractCommentableItemModel(Model model, Object item, ModelStructure modelStructure) {
        super(model, item, modelStructure)
        changedCommentListeners = []
        navigationListeners = []
        tabbedPaneChangeListeners = []
    }

    void addComment(Comment comment) {
        item.addComment(comment)
        commentChanged(comment)
    }

    void removeComment(Comment comment) {
        item.removeComment(comment)
        commentChanged(comment)
    }

    void removeCommentsByPath(String path) {
        def commentsToRemove = []
        item.comments.each {Comment comment ->
            if (comment.path.startsWith(path)) {
                commentsToRemove << comment
            }
        }
        commentsToRemove.each {Comment comment ->
            item.removeComment(comment)
        }
        changedCommentListeners.each {ChangedCommentListener listener ->
            listener.updateCommentVisualization()
        }

    }

    void commentChanged(Comment comment) {
        item.notifyItemChanged()
        changedCommentListeners.each {ChangedCommentListener listener ->
            listener.updateCommentVisualization()
        }
        if (comment) {
            String path = comment.getPath()
            def node = CommentAndErrorView.findNodeForPath(getTableTreeModel().root, path)
            if (!node) return
            node.comments.remove(comment)
            if (!comment.deleted)
                node.comments << comment
            getTableTreeModel().nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
    }

    void commentsChanged(List<Comment> comments) {
        for (Comment comment: comments) {
            String path = comment.getPath()
            def node = CommentAndErrorView.findNodeForPath(getTableTreeModel().root, path)
            node.comments << comment
            getTableTreeModel().nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
    }

    TreePath getTreePath(String path) {
        def node = CommentAndErrorView.findNodeForPath(getTableTreeModel().root, path)
        return new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[])
    }

    boolean isNotEmpty(String path) {
        return item.comments.any {it.path == path && !it.deleted && commentIsVisible(it)}
    }

    ITableTreeModel getTableTreeModel() {
        return treeModel
    }

    void addChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners << listener
        getTableTreeModel().addChangedCommentListener listener
    }

    void removeChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners.remove(listener)
        getTableTreeModel().removeChangedCommentListener listener
    }

    void addNavigationListener(NavigationListener listener) {
        navigationListeners << listener
    }

    void removeNavigationListener(NavigationListener listener) {
        navigationListeners.remove(listener)
    }

    void navigationSelected(boolean comment) {
        navigationListeners.each {NavigationListener listener ->
            listener.showHiddenComments()
        }
    }

    void selectTab(int tabIndex) {
        navigationListeners.each {NavigationListener listener ->
            listener.selectTab(tabIndex)
        }
    }

    void showCommentsTab() {
        navigationListeners.each {NavigationListener listener ->
            listener.showComments()
        }
    }

    void addTabbedPaneChangeListener(TabbedPaneChangeListener listener) {
        tabbedPaneChangeListeners << listener
    }

    void removeTabbedPaneChangeListener(TabbedPaneChangeListener listener) {
        tabbedPaneChangeListeners.remove listener
    }

    void tabbedPaneChanged(CommentFilter filter) {
    }

    void removeInvisibleComments() {
        getTableTreeModel().commentsToBeDeleted.each {Comment comment ->
            item.removeComment(comment)
        }
    }

    boolean commentIsVisible(Comment comment) {
        return getTableTreeModel().commentIsVisible(comment)
    }

    public boolean isReadOnly() {
        return true
    }


}
