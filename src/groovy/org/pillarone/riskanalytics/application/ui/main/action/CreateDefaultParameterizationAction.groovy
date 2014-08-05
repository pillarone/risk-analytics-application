package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.DefaultParameterizationDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateDefaultParameterizationAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateDefaultParameterizationAction)

    CreateDefaultParameterizationAction(ULCTableTree tree) {
        super("CreateDefaultParameterization", tree)
    }

    public void doActionPerformed(ActionEvent event) {
        Model simulationModel = getSelectedModel()
        boolean hasOneParameterColumnOnly = simulationModel.maxNumberOfFullyDistinctPeriods() == 1
        DefaultParameterizationDialog dialog = new DefaultParameterizationDialog(UlcUtilities.getWindowAncestor(tree), hasOneParameterColumnOnly)
        dialog.title = dialog.getText("title")
        String name = dialog.nameInput.text?.trim() // PMO-2011 Avoid copy/paste or fat finger errors adding whitespace
        dialog.okAction = {
            if (!validate(name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "NotValidName")
                alert.show()
            } else if (ParameterizationDAO.findByNameAndModelClassName(name, simulationModel.class.name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniquesNamesRequired")
                alert.show()
            } else {
                try {
                    int periodCount = hasOneParameterColumnOnly ? 1 : (Integer) dialog.periodCount.value
                    Parameterization param = null
                    ParameterizationDAO.withTransaction { status ->
                        param = ParameterizationHelper.createDefaultParameterization(simulationModel, periodCount)
                        param.name = name
                        param.save()
                        param = ModellingItemFactory.getParameterization(param.dao)
                        param.load()
                    }
                    dialog.hide()
                    riskAnalyticsEventBus.post(new OpenDetailViewEvent(new ParameterizationUIItem(param)))
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
        def seps = ["/", "//", File.separator, "=", ":", "!"] //Forbid path separators or search operators in names
        if (seps.any { name.indexOf(it) != -1 }){
            return false
        }
        return true
    }

}