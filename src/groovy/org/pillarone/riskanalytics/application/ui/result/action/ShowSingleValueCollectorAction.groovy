package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorView
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowSingleValueCollectorAction extends ResourceBasedAction {

    ULCCloseableTabbedPane tabbedPane
    def rowHeaderTableTree
    SimulationRun simulationRun

    public ShowSingleValueCollectorAction(ULCCloseableTabbedPane tabbedPane, def rowHeaderTableTree, SimulationRun simulationRun) {
        super("ShowSingleValueCollector");
        this.tabbedPane = tabbedPane
        this.rowHeaderTableTree = rowHeaderTableTree
        this.simulationRun = simulationRun
    }

    void doActionPerformed(ActionEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {(it instanceof ResultTableTreeNode) && (it.collector == SingleValueCollectingModeStrategy.IDENTIFIER)} as List
        SingleValueCollectorTableTreeModel model = new SingleValueCollectorTableTreeModel(nodes, simulationRun)
        SingleCollectorView view = new SingleCollectorView(model)
        view.init()
        if (view.content) {
            tabbedPane.addTab("Single value view ", view.content)
            String toolTipText = "Single value view"
            tabbedPane.setToolTipTextAt(tabbedPane.tabCount - 1, toolTipText)
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }


}
