package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterInjector
import org.pillarone.riskanalytics.core.parameterization.ParameterWriter
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.comment.view.ChangedCommentListener
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.comment.view.TabbedPaneChangeListener
import org.pillarone.riskanalytics.application.util.LocaleResources

class ParameterViewModel extends AbstractModellingModel {

    ParameterizationTableTreeModel paramterTableTreeModel
    PropertiesViewModel propertiesViewModel

    List<ParameterValidationError> validationErrors = []
    private List<ChangedCommentListener> changedCommentListeners
    private List<TabbedPaneChangeListener> tabbedPaneChangeListeners
    private List<NavigationListener> navigationListeners

    public ParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
        propertiesViewModel = new PropertiesViewModel(parameterization)
        changedCommentListeners = []
        tabbedPaneChangeListeners = []
        navigationListeners = []
    }

    protected ITableTreeModel buildTree() {
        builder = new ParameterizationTreeBuilder(model, structure, item)
        periodCount = builder.periodCount
        paramterTableTreeModel = new ParameterizationTableTreeModel(builder)
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.addValueChangedListener(
                [valueChanged: {Object node, int column -> item.changed = true}] as TableTreeValueChangedListener)
        paramterTableTreeModel.readOnly = !item.isEditable()
        return paramterTableTreeModel
    }

    void save() {
        ParameterWriter writer = new ParameterWriter()
        File file = new File("${modellingFileName}.groovy")

        File tmpFile = File.createTempFile("tmpParam", ".groovy")
        writer.write(builder.parameterConfigObject, tmpFile.newWriter())
        try {
            new ParameterInjector(tmpFile.absolutePath - ".groovy")
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in parameter. Can not write parameter file:${file.name}. original message: ${e.message}")
        }


        writer.write(builder.parameterConfigObject, file.newWriter())
    }

    ParameterizationTableTreeNode findNodeForPath(String path) {
        String[] pathElements = path.split(":")
        SimpleTableTreeNode currentNode = paramterTableTreeModel.root
        for (String p in pathElements) {
            currentNode = currentNode?.getChildByName(p)
        }
        return currentNode
    }

    void addErrors(List<ParameterValidationError> validationErrors) {
        for (ParameterValidationError error in validationErrors) {
            ParameterizationTableTreeNode node = findNodeForPath(error.getPath())
            node.errorMessage = error.getLocalizedMessage(LocaleResources.getLocale())
            paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
        for (ParameterValidationError previousError in this.validationErrors) {
            ParameterValidationError currentError = validationErrors.find { it.path == previousError.path}
            //Error is resolved now
            if (currentError == null) {
                ParameterizationTableTreeNode node = findNodeForPath(previousError.getPath())
                //if node is null, the error node was removed
                if (node != null) {
                    node.errorMessage = null
                    paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
                }
            }
        }
        this.validationErrors = validationErrors
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

    void setReadOnly(boolean value) {
        paramterTableTreeModel.readOnly = value
    }

    void addChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners << listener
        paramterTableTreeModel.addChangedCommentListener listener
    }

    void removeChangedCommentListener(ChangedCommentListener listener) {
        changedCommentListeners.remove(listener)
        paramterTableTreeModel.removeChangedCommentListener listener
    }

    void addTabbedPaneChangeListener(TabbedPaneChangeListener listener) {
        tabbedPaneChangeListeners << listener
    }

    void removeTabbedPaneChangeListener(TabbedPaneChangeListener listener) {
        tabbedPaneChangeListeners.remove listener
    }

    void tabbedPaneChanged(CommentFilter filter) {
        tabbedPaneChangeListeners.each {TabbedPaneChangeListener listener ->
            listener.tabbedPaneChanged(filter)
        }
    }

    void addNavigationListener(NavigationListener listener) {
        navigationListeners << listener
    }

    void removeNavigationListener(NavigationListener listener) {
        navigationListeners.remove(listener)
    }



    void commentChanged(Comment comment) {
        item.changed = true
        changedCommentListeners.each {ChangedCommentListener listener ->
            listener.updateCommentVisualization()
        }
        if (comment) {
            String path = comment.getPath()
            def node = CommentAndErrorView.findNodeForPath(paramterTableTreeModel.root, path.substring(path.indexOf(":") + 1))
            node.comments.remove(comment)
            if (!comment.deleted)
                node.comments << comment
            if (paramterTableTreeModel.root.path == path)
                paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(paramterTableTreeModel.root) as Object[]), 0)
            else
                paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
    }

    void commentsChanged(List<Comment> comments) {
        for (Comment comment: comments) {
            String path = comment.getPath()
            def node = CommentAndErrorView.findNodeForPath(paramterTableTreeModel.root, path.substring(path.indexOf(":") + 1))
            node.comments << comment
            paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
    }

    boolean isNotEmpty(String path) {
        return item.comments.any {it.path == path && !it.deleted && commentIsVisible(it)}
    }

    void navigationSelected(boolean comment) {
        navigationListeners.each {NavigationListener listener ->
            listener.showHiddenComments()
        }
    }

    void showCommentsTab() {
        navigationListeners.each {NavigationListener listener ->
            listener.showComments()
        }
    }

    void removeInvisibleComments() {
        paramterTableTreeModel.commentsToBeDeleted.each {Comment comment ->
            item.removeComment(comment)
        }
    }

    boolean commentIsVisible(Comment comment) {
        return paramterTableTreeModel.commentIsVisible(comment)
    }

}


