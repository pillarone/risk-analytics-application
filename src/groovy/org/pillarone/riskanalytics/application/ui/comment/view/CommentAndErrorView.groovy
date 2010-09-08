package org.pillarone.riskanalytics.application.ui.comment.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentAndErrorView implements CommentListener, NavigationListener {

    ULCCloseableTabbedPane tabbedPane
    ErrorPane errorPane
    private ParameterViewModel model;
    Map openItems
    boolean tabbedPaneVisible = true


    public CommentAndErrorView(ParameterViewModel model) {
        this.model = model;
        initComponents()
        layoutComponents()
        attachListeners()
        openItems = [:]
        this.model.addNavigationListener this
    }

    protected void initComponents() {
        tabbedPane = new ULCDetachableTabbedPane(name: "commentAndErrorPane")
        errorPane = new ErrorPane(model)
    }

    private void layoutComponents() {
        ShowCommentsView view = new ShowCommentsView(this, null)
        model.addChangedCommentListener view
        ULCBoxPane content = new ULCBoxPane(1, 3)
        CommentSearchPane commentSearchPane = new CommentSearchPane(view.container, errorPane.container, model)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, commentSearchPane.content)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, errorPane.container)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, view.container)
        tabbedPane.addTab("Validations and comments", new ULCScrollPane(content))
        tabbedPane.setCloseableTab(0, false)
    }

    void attachListeners() {
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            int closingIndex = event.getTabClosingIndex()
            def modelCardContent = event.getClosableTabbedPane()
            ULCComponent currentComponent = modelCardContent.getComponentAt(closingIndex)
            def view = openItems[currentComponent]
            if (view && view instanceof ShowCommentsView) {
                model.removeChangedCommentListener view
            }
            openItems.remove(currentComponent)
            modelCardContent.removeTabAt closingIndex
        }] as ITabListener)
    }

    protected void updateErrorVisualization(Parameterization item) {
        item.validate()
        errorPane.clear()
        errorPane.addErrors item.validationErrors
        model.addErrors(item.validationErrors)
    }

    public void addNewCommentView(String path, int periodIndex) {
        NewCommentView view = new NewCommentView(this, path, periodIndex)
        openItems[view.content] = view
        String tabTitle = getDisplayName(model, path)
        tabTitle += ((periodIndex == -1) ? " for all periods" : " P" + periodIndex)
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            int tabIndex = tabbedPane.tabCount
            tabbedPane.addTab(tabTitle, view.content)
            tabbedPane.setCloseableTab(tabIndex, true)
            tabbedPane.setToolTipTextAt(tabIndex, getDisplayPath(model, path))
            tabbedPane.selectedIndex = tabIndex
        }
    }

    public void editCommentView(Comment comment) {
        int index = getTabIndex(comment, null, null)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            int tabIndex = tabbedPane.tabCount
            EditCommentView view = new EditCommentView(this, comment)
            openItems[view.content] = view
            String tabTitle = getDisplayName(model, comment.path) + " P" + comment.period
            tabTitle += ((comment.period == -1) ? " for all periods" : " P" + comment.period)
            tabbedPane.addTab(tabTitle, view.content)
            tabbedPane.setCloseableTab(tabIndex, true)
            tabbedPane.setToolTipTextAt(tabIndex, getDisplayPath(model, comment.path))
            tabbedPane.selectedIndex = tabIndex
        }

    }

    public void showCommentsView(String path, int periodIndex) {
        String tabTitle = path ? getDisplayName(model, path) : "Comments"
        int index = getTabIndex(null, path, "Comments")
        if (index >= 0 && (!path || tabbedPane.getToolTipTextAt(index) == getDisplayPath(model, path))) {
            tabbedPane.selectedIndex = index
        } else {
            int tabIndex = tabbedPane.tabCount
            ShowCommentsView view = new ShowCommentsView(this, path)
            openItems[view.content] = view
            model.addChangedCommentListener view
            tabbedPane.addTab(tabTitle, UIUtils.getIcon("comment.png"), view.content)
            tabbedPane.setCloseableTab(tabIndex, true)
            tabbedPane.setToolTipTextAt(tabIndex, getDisplayPath(model, path))
            tabbedPane.selectedIndex = tabIndex
        }
    }

    public void showErrorsView() {
        String tabTitle = "Validations"
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            int tabIndex = tabbedPane.tabCount
            ErrorPane errorPane = new ErrorPane(model)
            errorPane.addErrors model.item.validationErrors
            tabbedPane.addTab(tabTitle, errorPane.content)
            tabbedPane.setCloseableTab(tabIndex, true)
            tabbedPane.selectedIndex = tabIndex
        }
    }

    public void commentsSelected() {
        this.tabbedPaneVisible = !tabbedPaneVisible
        tabbedPane.setVisible this.tabbedPaneVisible
    }


    static String getDisplayPath(def model, String path) {
        if (!path) return ""
        def node = findNodeForPath(model.paramterTableTreeModel.root, path.substring(path.indexOf(":") + 1))
        return node.getDisplayPath()
    }

    static String getDisplayName(def model, String path) {
        if (!path) return ""
        def node = findNodeForPath(model.paramterTableTreeModel.root, path.substring(path.indexOf(":") + 1))
        String displayName = node.getDisplayName()
        if (!displayName) {
            displayName = getDisplayPath(model, path)
        }
        return displayName ? displayName : path
    }

    static def findNodeForPath(def root, String path) {
        String[] pathElements = path.split(":")
        SimpleTableTreeNode currentNode = root
        for (String p in pathElements) {
            currentNode = currentNode?.getChildByName(p)
        }
        return currentNode
    }

    void closeTab() {
        tabbedPane.removeTabAt tabbedPane.getSelectedIndex()
    }

    private int getTabIndex(Comment comment, String path, String tabTitle) {
        int index = -1
        openItems.each {k, v ->
            if (v instanceof EditCommentView) {
                if (v.comment.equals(comment)) {
                    index = tabbedPane.indexOfComponent(k)
                }
            } else if (v instanceof ShowCommentsView) {
                if (path && v.path == path) {
                    index = tabbedPane.indexOfComponent(k)
                }
                if (index == -1)
                    index = tabbedPane.indexOfTab(tabTitle)
            }
        }
        return index
    }

}
