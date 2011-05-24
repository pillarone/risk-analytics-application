package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView

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

        model.resultStructureChanged()
        modellingTreeView.setModel(model)
        modellingTreeView.filterSelection.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
        modellingTreeView.filterLabel.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
    }
}

