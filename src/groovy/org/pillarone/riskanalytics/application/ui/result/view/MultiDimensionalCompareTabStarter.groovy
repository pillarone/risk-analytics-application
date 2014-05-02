package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.parameterization.action.TabStarter
import org.pillarone.riskanalytics.application.ui.parameterization.model.CompareParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterCompareViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterCompareView
import org.pillarone.riskanalytics.application.ui.parameterization.view.TabIdentifier
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem

class MultiDimensionalCompareTabStarter extends ExceptionSafeAction {

    CompareParameterizationsView parameterView
    Map openTabs = [:]

    public MultiDimensionalCompareTabStarter(CompareParameterizationsView parameterView) {
        this.@parameterView = parameterView
        attachListeners()
    }

    public void doActionPerformed(ActionEvent event) {
        ULCTableTree tree = event.source
        def lastComponent = tree?.selectedPath?.lastPathComponent
        if (lastComponent instanceof CompareParameterizationTableTreeNode) {

            if (lastComponent.parameterizationTableTreeNode instanceof MultiDimensionalParameterizationTableTreeNode) {
                TabIdentifier identifier = new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn)
                def index = openTabs.get(identifier)
                ULCTabbedPane tabbedPane = parameterView.tabbedPane

                if (index == null) {

                    List<ParametrizedItem> parametrizedItems = lastComponent.itemsToCompare
                    List<ParameterHolder> referenceParameters = parametrizedItems[1].getParameterHoldersForAllPeriods(lastComponent.parameterPath) //0 index seems to be for the tree
                    List<List<MultiDimensionalParameterHolder>> parametersToCompare = []
                    for(int i = 2; i < parametrizedItems.size(); i++) {
                        parametersToCompare << parametrizedItems[i].getParameterHoldersForAllPeriods(lastComponent.parameterPath)
                    }

                    int periodIndex = lastComponent.getPeriodIndex(tree.selectedColumn + 1)

                    def reference = referenceParameters[periodIndex]?.businessObject
                    if(reference == null) {
                        return
                    }
                    List<AbstractMultiDimensionalParameter> toCompare = []
                    for(List<MultiDimensionalParameterHolder> list in parametersToCompare) {
                        if(list.size() > periodIndex) {
                            toCompare << list.get(periodIndex)?.businessObject
                        } else {
                            toCompare << null
                        }
                    }
                    MultiDimensionalParameterCompareViewModel model = new MultiDimensionalParameterCompareViewModel(reference, toCompare, parametrizedItems.subList(1, parametrizedItems.size()), periodIndex)
                    tabbedPane.addTab(lastComponent.parameterizationTableTreeNode.displayName, UIUtils.getIcon(UIUtils.getText(TabStarter, "MDP.icon")), new MultiDimensionalParameterCompareView(model).content)
                    int currentTab = tabbedPane.tabCount - 1
                    tabbedPane.selectedIndex = currentTab
                    openTabs.put(new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn), currentTab)
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
    }

    protected void attachListeners() {
        Closure closeAction = { event ->
            int index = parameterView.tabbedPane.getSelectedIndex()
            if (parameterView.tabbedPane.isCloseable(index)) {
                parameterView.tabbedPane.closeCloseableTab(index)
                removeTab(index)
            }
        }
        parameterView.tabbedPane.addTabListener([tabClosing: {TabEvent event -> removeTab(event.getTabClosingIndex()); parameterView.tabbedPane.closeCloseableTab(event.getTabClosingIndex()) }] as ITabListener)
        parameterView.tabbedPane.registerKeyboardAction([actionPerformed: closeAction] as IActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, false),
                ULCComponent.WHEN_IN_FOCUSED_WINDOW)

    }

    private void removeTab(int index) {
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
