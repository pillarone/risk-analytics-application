package org.pillarone.riskanalytics.application.ui.main.action

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SelectionTreeAction {
    public CreateNewMajorVersion(ULCTableTree tree, P1RATModel model) {
        super("NewMajorVersion", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        createNewVersion(getSelectedItem())
    }

    private void createNewVersion(Parameterization item) {
        model.createNewVersion(selectedModel, item, false)
    }


    private void createNewVersion(ResultConfiguration template) {
        model.createNewVersion(selectedModel, template)
    }

    private void createNewVersion(def node) {}

}
