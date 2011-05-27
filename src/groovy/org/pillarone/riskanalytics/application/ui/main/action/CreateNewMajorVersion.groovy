package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SelectionTreeAction {
    public CreateNewMajorVersion(ULCTableTree tree, P1RATModel model) {
        super("NewMajorVersion", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        ModellingItem item = getSelectedItem()
        if (item instanceof Parameterization) {
            Closure okAction = {ModellingItem modellingItem, String commentText ->
                createNewVersion(modellingItem, commentText)
            }
            NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(tree, item, okAction)
            versionCommentDialog.show()
        } else
            createNewVersion(getSelectedItem(), null)

    }

    private ModellingItem createNewVersion(Parameterization item, String commentText) {
        return model.createNewVersion(selectedModel, item, commentText)
    }


    private ModellingItem createNewVersion(ResultConfiguration template, String commentText) {
        return model.createNewVersion(selectedModel, template, commentText)
    }

    private ModellingItem createNewVersion(def node, String commentText) { return null}

}
