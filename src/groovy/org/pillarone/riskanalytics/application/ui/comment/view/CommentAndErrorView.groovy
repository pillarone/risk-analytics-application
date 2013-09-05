package org.pillarone.riskanalytics.application.ui.comment.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.model.UndockedPaneListener
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentAndErrorView implements CommentListener {

    ULCCloseableTabbedPane tabbedPane
    CommentSearchPane commentSearchPane
    ErrorPane errorPane
    ULCBoxPane content
    AbstractCommentableItemModel model;
    Map openItems
    ULCFixedColumnTableTree tableTree

    public CommentAndErrorView(AbstractCommentableItemModel model) {
        this.model = model;
        initComponents()
        layoutComponents()
        attachListeners()
        openItems = [:]
    }

    protected void initComponents() {
        tabbedPane = new ULCDetachableTabbedPane(name: "commentAndErrorPane")
        errorPane = new ErrorPane(model, this)
    }

    private void layoutComponents() {
        ShowCommentsView view = new ShowCommentsView(this, null)
        view.addAllComments()
        model.addChangedCommentListener view
        model.addTabbedPaneChangeListener view
        model.addTabbedPaneChangeListener errorPane
        ShowCommentsView result = new ShowCommentsView(this, null)
        model.addChangedCommentListener result
        content = new ULCBoxPane(1, 2)
        commentSearchPane = new CommentSearchPane(view, errorPane, result, model)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, commentSearchPane.content)
        ULCBoxPane scrolledPane = new ULCBoxPane(1, 3)
        scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, errorPane.container)
        scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, view.container)
        scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, result.container)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(scrolledPane))
        tabbedPane.addTab(UIUtils.getText(this.class, "ValidationsAndComments"), content)
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

    void addPopupMenuListener(Closure closeSplitPane) {
        ((ULCPopupMenu) tabbedPane.getComponentPopupMenu())?.addPopupMenuListener(new UndockedPaneListener(closeSplitPane: closeSplitPane))
    }

    void updateErrorVisualization(Parameterization item) {
        item.validate()
        errorPane.clear()
        errorPane.addErrors item.validationErrors
        model.addErrors(item.validationErrors)
    }

    protected void updateErrorVisualization(Simulation item) {
    }

    public void addNewCommentView(String path, int periodIndex) {
        model.showCommentsTab()
        NewCommentView view = new NewCommentView(this, path, periodIndex)
        openItems[view.content] = view
        String tabTitle = getDisplayName(model, path)
        tabTitle += ((periodIndex == -1) ? " " + UIUtils.getText(this.class, "forAllPeriods") : " P" + periodIndex)
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            ULCBoxPane scrolledPane = new ULCBoxPane(1, 1)
            scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(view.content))
            addTab(scrolledPane, tabTitle, getDisplayPath(model, path))
            view.commentTextArea.requestFocus()
        }
    }

    public void addNewFunctionCommentView(List functionsMap) {
        model.showCommentsTab()
        NewFunctionCommentView view = new NewFunctionCommentView(this, functionsMap)
        openItems[view.content] = view
        String tabTitle = UIUtils.getText(CommentAndErrorView, "addFunctionComment")
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            ULCBoxPane scrolledPane = new ULCBoxPane(1, 1)
            scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(view.content))
            addTab(scrolledPane, tabTitle, "")
            view.commentTextArea.requestFocus()
        }
    }

    public void addNewIssueView(String path, int periodIndex) {
        NewCommentView view = new NewIssueView(this, path, periodIndex)
        openItems[view.content] = view
        String tabTitle = getDisplayName(model, path)
        tabTitle += ((periodIndex == -1) ? " for all periods" : " P" + periodIndex)
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            ULCBoxPane scrolledPane = new ULCBoxPane(1, 1)
            scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(view.content))
            addTab(scrolledPane, tabTitle, getDisplayPath(model, path))
        }
    }

    public void editCommentView(Comment comment) {
        int index = getTabIndex(comment, null, null)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            EditCommentView view = new EditCommentView(this, comment)
            openItems[view.content] = view
            String tabTitle = getDisplayName(model, comment.path)
            tabTitle += ((comment.period == -1) ? " " + UIUtils.getText(this.class, "forAllPeriods") : " P" + comment.period)
            ULCBoxPane scrolledPane = new ULCBoxPane(1, 1)
            scrolledPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(view.content))
            addTab(scrolledPane, tabTitle, getDisplayPath(model, comment.path))
        }

    }

    public void showCommentsView(String path, int periodIndex) {
        model.showCommentsTab()
        String tabTitle = path ? getDisplayName(model, path) : UIUtils.getText(CommentSearchPane.class, "comments")
        int index = getTabIndex(null, path, UIUtils.getText(CommentSearchPane.class, "comments"))
        if (index >= 0 && (!path || tabbedPane.getToolTipTextAt(index) == getDisplayPath(model, path))) {
            tabbedPane.selectedIndex = index
        } else {
            ShowCommentsView view = new ShowCommentsView(this, path)
            view.addAllComments()
            openItems[view.content] = view
            model.addChangedCommentListener view
            addTab(view.content, tabTitle, getDisplayPath(model, path), UIUtils.getIcon("comment.png"))
        }
    }

    public void showErrorsView() {
        model.showCommentsTab()
        String tabTitle = UIUtils.getText(this.class, "Validations")
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            ErrorPane errorPane = new ErrorPane(model)
            errorPane.addErrors model.item.validationErrors
            addTab(errorPane.content, tabTitle, "")
        }
    }

    public void showErrorAndCommentsView() {
        String tabTitle = UIUtils.getText(this.class, "ValidationsAndComments")
        int index = tabbedPane.indexOfTab(tabTitle)
        if (index >= 0) {
            tabbedPane.selectedIndex = index
        } else {
            tabbedPane.insertTab(tabTitle, null, new ULCScrollPane(content), "", 0)
            tabbedPane.selectedIndex = 0
        }
    }




    static String getDisplayPath(def model, String path) {
        if (!path) return ""
        def node = findNodeForPath(model.getTableTreeModel().root, path.substring(path.indexOf(":") + 1))
        if (!node) node = findNodeForPath(model.getTableTreeModel().root, path)
        return node?.getDisplayPath() ? node?.getDisplayPath() : path
    }

    static String getDisplayName(def model, String path) {
        if (!path) return ""
        def node = findNodeForPath(model.getTableTreeModel().root, path.substring(path.indexOf(":") + 1))
        if (!node) node = findNodeForPath(model.getTableTreeModel().root, path)
        String displayName = node?.getDisplayName()
        if (!displayName) {
            displayName = getDisplayPath(model, path)
        }
        return displayName ? displayName : path
    }

    static ITableTreeNode findNodeForPath(ITableTreeNode root, String path) {
        def nodeForPath = null
        if ((root instanceof ResultTableTreeNode) && root.getResultPath() == path) {
            nodeForPath = root
        } else if (!(root instanceof ResultTableTreeNode) && root.path == path) {
            nodeForPath = root
        } else {
            for (int i = 0; i < root.childCount && nodeForPath == null; i++) {
                def childNode = root.getChildAt(i)
                nodeForPath = findNodeForPath(childNode, path)
            }
        }
        return nodeForPath
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

    private void addTab(ULCComponent content, String tabTitle, String toolTip, ULCIcon icon = null) {
        int tabIndex = tabbedPane.tabCount
        icon ? tabbedPane.addTab(tabTitle, icon, content) : tabbedPane.addTab(tabTitle, content)
        tabbedPane.setCloseableTab(tabIndex, true)
        tabbedPane.setToolTipTextAt(tabIndex, toolTip)
        tabbedPane.selectedIndex = tabIndex
    }

}
