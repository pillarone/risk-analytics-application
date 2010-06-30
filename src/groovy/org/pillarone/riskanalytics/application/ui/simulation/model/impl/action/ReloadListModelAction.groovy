package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel

/**
 * Action which is used when a new parameterization or result configuration name is selected.
 * It then synchronizes the version combo boxes with the new items.
 */
class ReloadListModelAction implements IActionListener {

    private SimulationSettingsPaneModel model

    public ReloadListModelAction(SimulationSettingsPaneModel model) {
        this.model = model;
    }

    void actionPerformed(ActionEvent actionEvent) {
        String currentSelection = model.parameterizationNames.selectedItem
        model.parameterizationVersions.reload(currentSelection)

        currentSelection = model.resultConfigurationNames.selectedItem
        model.resultConfigurationVersions.reload(currentSelection)
    }


}
