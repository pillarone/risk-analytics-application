package org.pillarone.riskanalytics.application.ui.parameterization.action

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterView
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.parameterization.view.TabIdentifier
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

class MultiDimensionalTabStarter implements IActionListener {

    ParameterView parameterView
    Map openTabs = [:]

    public MultiDimensionalTabStarter(ParameterView parameterView) {
        this.@parameterView = parameterView
        parameterView.tabbedPane.addTabListener(
                [tabClosing: {
                    TabEvent event ->
                    int index = event.getTabClosingIndex()

                    for (Iterator it = openTabs.iterator(); it.hasNext();) {
                        Map.Entry entry = it.next();
                        if (entry.value > index) {
                            entry.value--
                        } else if (entry.value == index) {
                            it.remove()
                        }
                    }
                }
                ] as ITabListener)

    }

    public void actionPerformed(ActionEvent event) {
        ULCTableTree tree = event.source
        def lastComponent = tree.getSelectedPath().lastPathComponent

        if (lastComponent instanceof MultiDimensionalParameterizationTableTreeNode) {
            TabIdentifier identifier = new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn)
            def index = openTabs.get(identifier)
            ULCTabbedPane tabbedPane = parameterView.tabbedPane

            if (index == null) {
                MultiDimensionalParameterModel model = new MultiDimensionalParameterModel(tree.model, lastComponent, tree.selectedColumn + 1)
                model.tableModel.readOnly = parameterView.model.treeModel.readOnly
                ClientContext.setModelUpdateMode(model.tableModel, UlcEventConstants.SYNCHRONOUS_MODE)
                model.tableModel.addListener([modelChanged: { parameterView.model.item.changed = true }] as IModelChangedListener)
                parameterView.model.item.changed = false
                tabbedPane.addTab("${lastComponent.displayName} ${tree.getColumnModel().getColumn(tree.getSelectedColumn()).getHeaderValue()}", UIUtils.getIcon(UIUtils.getText(this.class, "MDP.icon")), new MultiDimensionalParameterView(model).content)
                int currentTab = tabbedPane.tabCount - 1
                tabbedPane.selectedIndex = currentTab
                tabbedPane.setToolTipTextAt(currentTab, model.getPathAsString())
                openTabs.put(new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn), currentTab)
            } else {
                tabbedPane.selectedIndex = index
            }
        } else {
            int selectedRow = tree.selectedRow
            if (selectedRow + 1 <= tree.rowCount) {
                tree.selectionModel.setSelectionPath(tree.getPathForRow(selectedRow + 1))
            }
        }
    }

}