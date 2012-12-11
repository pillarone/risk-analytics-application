package org.pillarone.riskanalytics.application.ui.result.action

import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.view.ExportRawDataTable


class ExportResultIterationDataAction extends ResourceBasedAction {

    private ULCTableTree tree
    private SimulationRun run

    private ExportRawDataTable innerAction

    ExportResultIterationDataAction(ULCTableTree tree, SimulationRun run) {
        super("ExportRawDataTable")
        this.tree = tree
        this.run = run
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        List<SimpleTableTreeNode> nodes = tree.selectedPaths*.lastPathComponent
        ResultIterationDataViewModel model = new ResultIterationDataViewModel(run, nodes, false, true, true, null)
        model.query()
        innerAction = new ExportRawDataTable(model, tree)
        innerAction.doActionPerformed(null)
    }
}
