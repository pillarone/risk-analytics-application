package org.pillarone.riskanalytics.application.ui.comment.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentAndErrorView implements CommentListener {

    ULCCloseableTabbedPane tabbedPane
    ErrorPane errorPane
    private ParameterViewModel model;

    public CommentAndErrorView(ParameterViewModel model) {
        this.model = model;
        initComponents()
        layoutComponents()
        attachListeners()

    }

    protected void initComponents() {
        tabbedPane = new ULCDetachableTabbedPane()
        errorPane = new ErrorPane(model)
    }

    private void layoutComponents() {
        tabbedPane.addTab("Errors", errorPane.content)
        tabbedPane.setCloseableTab(0, false)
    }

    void attachListeners() {
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            int closingIndex = event.getTabClosingIndex()
            tabbedPane.removeTabAt closingIndex
        }] as ITabListener)
    }

    void addCommentPane() {

    }

    protected void updateErrorVisualization(Parameterization item) {
        item.validate()
        errorPane.clear()
        errorPane.addErrors item.validationErrors
        model.addErrors(item.validationErrors)
    }

    public void addNewCommentView(String path, int periodIndex, String displayPath) {
        int tabIndex = tabbedPane.tabCount
        tabbedPane.addTab(getNodeName(displayPath, periodIndex), new NewCommentView(this, path, periodIndex, displayPath).content)
        tabbedPane.setCloseableTab(tabIndex, true)
        tabbedPane.setToolTipTextAt(tabIndex, displayPath)
        tabbedPane.selectedIndex = tabIndex
    }

    public void editCommentView(Comment comment) {
        int tabIndex = tabbedPane.tabCount
        tabbedPane.addTab(getDisplayName(comment.path), new EditCommentView(this, comment).content)
        tabbedPane.setCloseableTab(tabIndex, true)
        tabbedPane.setToolTipTextAt(tabIndex, getDisplayPath(comment.path))
        tabbedPane.selectedIndex = tabIndex
    }

    public void showCommentsView(String path, int periodIndex, String displayPath) {
        int tabIndex = tabbedPane.tabCount
        tabbedPane.addTab(getNodeName(displayPath, periodIndex), new ShowCommentsView(this).content)
        tabbedPane.setCloseableTab(tabIndex, true)
        tabbedPane.setToolTipTextAt(tabIndex, displayPath)
        tabbedPane.selectedIndex = tabIndex
    }




    private getNodeName(String displayPath, int periodIndex) {
        if (displayPath && displayPath.indexOf("/") != -1)
            return displayPath.substring(displayPath.lastIndexOf("/") + 1, displayPath.length()) + " P" + periodIndex
        return "P" + periodIndex
    }

    String getDisplayPath(String path) {
        return model.findNodeForPath(path.substring(path.indexOf(":") + 1)).getDisplayPath()
    }

    String getDisplayName(String path) {
        return model.findNodeForPath(path.substring(path.indexOf(":") + 1)).getDisplayName()
    }

    void closeTab() {
        tabbedPane.removeTabAt tabbedPane.getSelectedIndex()
    }

}
