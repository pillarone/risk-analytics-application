package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.DefaultParameterizationDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateDefaultParameterizationAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateDefaultParameterizationAction)

    public CreateDefaultParameterizationAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
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
                    Parameterization param
                    ParameterizationDAO.withTransaction {status ->
                        param = ParameterizationHelper.createDefaultParameterization(simulationModel, periodCount)
                        param.name = dialog.nameInput.text
                        param.save()
                        param = ModellingItemFactory.getItem(param.dao, param.modelClass)
                    }
                    dialog.hide()

                    ParameterizationUIItem parameterizationUIItem = new ParameterizationUIItem(null, param)
                    model.fireModelChanged()
                    parameterizationUIItem.load(true)
                    model.notifyOpenDetailView(simulationModel, parameterizationUIItem)
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