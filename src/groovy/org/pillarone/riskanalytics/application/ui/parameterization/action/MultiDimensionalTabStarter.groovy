package org.pillarone.riskanalytics.application.ui.parameterization.action

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.comment.model.CommentPathFilter
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterView
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.parameterization.view.TabIdentifier
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCComponent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

class MultiDimensionalTabStarter implements IActionListener {

    ParameterView parameterView
    Map openTabs = [:]

    public MultiDimensionalTabStarter(ParameterView parameterView) {
        this.@parameterView = parameterView
        attachListeners()
    }

    public void actionPerformed(ActionEvent event) {
        ULCTableTree tree = event.source
        // most probably necessary due to https://www.canoo.com/jira/browse/UBA-7909
        // http://www.canoo.com/jira/browse/UBA-7580
        def lastComponent = tree?.selectedPath?.lastPathComponent

        if (lastComponent instanceof MultiDimensionalParameterizationTableTreeNode) {
            TabIdentifier identifier = new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn)
            def index = openTabs.get(identifier)
            ULCTabbedPane tabbedPane = parameterView.tabbedPane

            if (index == null) {
                MultiDimensionalParameterModel model = new MultiDimensionalParameterModel(tree.model, lastComponent, tree.selectedColumn + 1)
                model.tableModel.readOnly = parameterView.model.treeModel.readOnly
                ClientContext.setModelUpdateMode(model.tableModel, UlcEventConstants.SYNCHRONOUS_MODE)
                tabbedPane.addTab("${lastComponent.displayName} ${tree.getColumnModel().getColumn(tree.getSelectedColumn()).getHeaderValue()}", UIUtils.getIcon(UIUtils.getText(this.class, "MDP.icon")), new MultiDimensionalParameterView(model).content)
                int currentTab = tabbedPane.tabCount - 1
                tabbedPane.selectedIndex = currentTab
                tabbedPane.setToolTipTextAt(currentTab, model.getPathAsString())
                openTabs.put(new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn), currentTab)
                parameterView.addCommentFilter(currentTab, new CommentPathFilter(tree?.selectedPath?.lastPathComponent?.path))
            } else {
                tabbedPane.selectedIndex = index
            }
        } else {
            if (tree.selectedRow) {
                int selectedRow = tree.selectedRow
                if (selectedRow + 1 <= tree.rowCount) {
                    tree.selectionModel.setSelectionPath(tree.getPathForRow(selectedRow + 1))
                }
            }
        }
    }

    protected void attachListeners() {
        Closure closeAction = { event ->
            int index = parameterView.tabbedPane.getSelectedIndex()
            if (parameterView.tabbedPane.isCloseable(index)) {
                parameterView.tabbedPane.closeCloseableTab(index)
                removeTab(index)
            }
        }
        parameterView.tabbedPane.addTabListener([tabClosing: {TabEvent event -> removeTab(event.getTabClosingIndex()) }] as ITabListener)
        parameterView.tabbedPane.registerKeyboardAction([actionPerformed: closeAction] as IActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, false),
                ULCComponent.WHEN_IN_FOCUSED_WINDOW)

    }

    private void removeTab(int index) {
        parameterView.commentFilters[index] = null
        for (Iterator it = openTabs.iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            if (entry.value > index) {
                entry.value--
            } else if (entry.value == index) {
                it.remove()
            }
        }
    }

}