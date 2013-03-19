package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.DeterministicResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel

class ApplySelectionAction extends ResourceBasedAction {

    AbstractModellingModel model
    AbstractModellingTreeView modellingTreeView

    public ApplySelectionAction(AbstractModellingModel model, AbstractModellingTreeView view) {
        super("ApplySelectionAction")
        this.model = model
        this.modellingTreeView = view
    }

    public void doActionPerformed(ActionEvent event) {
        //remove the action listener because the view is re-initialized and the same action instance used as listener in the new combo box
        modellingTreeView.selectView.removeActionListener(this)

        handleModel(model)
        modellingTreeView.filterSelection.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
        modellingTreeView.filterLabel.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
    }

    protected void handleModel(AbstractModellingModel model) {
        modellingTreeView.setModel(model)
    }

    protected void handleModel(ResultViewModel model) {
        model.resultStructureChanged()
        modellingTreeView.setModel(model)
        model.addFunction(new MeanFunction())
    }

    protected void handleModel(DeterministicResultViewModel model) {
        model.resultStructureChanged()
        modellingTreeView.setModel(model)
    }

    protected void handleModel(CompareSimulationsViewModel model) {
        model.resultStructureChanged()
        modellingTreeView.setModel(model)
    }
}

