package org.pillarone.riskanalytics.application.ui.main.action

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.DefaultParameterizationDialog
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateDefaultParameterizationAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateDefaultParameterizationAction)

    public CreateDefaultParameterizationAction(ULCTableTree tree, P1RATModel model) {
        super("CreateDefaultParameterization", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model simulationModel = getSelectedModel()
        boolean hasOneParameterColumnOnly = simulationModel.maxNumberOfFullyDistinctPeriods() == 1
        DefaultParameterizationDialog dialog = new DefaultParameterizationDialog(UlcUtilities.getWindowAncestor(tree), hasOneParameterColumnOnly)
        dialog.title = dialog.getText("title")
        dialog.okAction = {
            if (!validate(dialog.nameInput.text)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "NotValidName")
                alert.show()
            } else if (ParameterizationDAO.findByNameAndModelClassName(dialog.nameInput.text, simulationModel.class.name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniquesNamesRequired")
                alert.show()
            } else {
                try {
                    int periodCount = hasOneParameterColumnOnly ? 1 : (Integer) dialog.periodCount.value
                    def param = ParameterizationHelper.createDefaultParameterization(simulationModel, periodCount)
                    param.name = dialog.nameInput.text
                    param.save()
                    param = ModellingItemFactory.getItem(param.dao, param.modelClass)
                    dialog.hide()

                    model.selectionTreeModel.addNodeForItem(param)
                    model.fireModelChanged()
                } catch (Exception ex) {
                    LOG.error "Error creating default parameterization", ex
                    I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "CreationError")
                    alert.show()
                }
            }

        }
        dialog.show()

    }

    boolean validate(String name) {
        if (!name) return false
        def seps = ["/", "//", File.separator]
        if (seps.any {name.indexOf(it) != -1}) return false
        return true
    }

}