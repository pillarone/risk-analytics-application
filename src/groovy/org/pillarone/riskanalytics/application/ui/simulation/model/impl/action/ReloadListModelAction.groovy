package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel

/**
 * Action for the synchronization of the version comboBox with selected
 * parameterization or result configuration
 */

abstract class ReloadListModelAction implements IActionListener {
    protected SimulationSettingsPaneModel model

    public ReloadListModelAction(SimulationSettingsPaneModel model) {
        this.model = model;
    }

}

/**
 * Action which is used when a new parameterization is selected.
 * It then synchronizes the version combo boxes with the new items.
 */
class ReloadParameterizationListModelAction extends ReloadListModelAction {

    public ReloadParameterizationListModelAction(SimulationSettingsPaneModel model) {
        super(model);
    }

    void actionPerformed(ActionEvent actionEvent) {
        String currentSelection = model.parameterizationNames.selectedItem
        model.parameterizationVersions.reload(currentSelection)
    }

}

/**
 * Action which is used when a result configuration name is selected.
 * It then synchronizes the version combo boxes with the new items.
 */

class ReloadResultConfigurationListModelAction extends ReloadListModelAction {

    public ReloadResultConfigurationListModelAction(SimulationSettingsPaneModel model) {
        super(model);
    }

    void actionPerformed(ActionEvent actionEvent) {
        String currentSelection = model.resultConfigurationNames.selectedItem
        model.resultConfigurationVersions.reload(currentSelection)
        model.notifyConfigurationChanged()
    }

}
