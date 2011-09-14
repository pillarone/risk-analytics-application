package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.AggregatedWithSingleAvailableCollectingModeStrategy

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowSingleValueCollectorAction extends ResourceBasedAction {

    ULCCloseableTabbedPane tabbedPane
    def rowHeaderTableTree
    SimulationRun simulationRun
    Integer iteration

    public ShowSingleValueCollectorAction(String actionName) {
        super(actionName)
    }

    public ShowSingleValueCollectorAction(ULCCloseableTabbedPane tabbedPane, def rowHeaderTableTree, SimulationRun simulationRun) {
        super("ShowSingleValueCollector");
        this.tabbedPane = tabbedPane
        this.rowHeaderTableTree = rowHeaderTableTree
        this.simulationRun = simulationRun
    }


    void doActionPerformed(ActionEvent event) {
        SingleValueCollectorTableTreeModel model = new SingleValueCollectorTableTreeModel(getNodes(), simulationRun, true)
        if (iteration) {
            model.fromIteration = iteration
            model.iterations = iteration
        }
        SingleCollectorView view = new SingleCollectorView(model)
        view.init()
        if (view.content) {
            String title = UIUtils.getText(this.class, "SingleValueViewTitle")
            tabbedPane.addTab(title, view.content)
            String toolTipText = title
            tabbedPane.setToolTipTextAt(tabbedPane.tabCount - 1, toolTipText)
            tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        }
    }

    List getNodes() {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        return paths.findAll {(it instanceof ResultTableTreeNode) && (it.collector == AggregatedWithSingleAvailableCollectingModeStrategy.IDENTIFIER)} as List
    }


}
