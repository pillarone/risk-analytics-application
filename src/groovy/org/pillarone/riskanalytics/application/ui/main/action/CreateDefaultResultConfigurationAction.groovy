package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.DefaultResultConfigurationDialog
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel


class CreateDefaultResultConfigurationAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateDefaultResultConfigurationAction)

    CreateDefaultResultConfigurationAction(tree, RiskAnalyticsMainModel model) {
        super("CreateDefaultResultConfiguration", tree, model)
    }

    CreateDefaultResultConfigurationAction(String title) {
        super(title)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        DefaultResultConfigurationDialog dialog = new DefaultResultConfigurationDialog(UlcUtilities.getWindowAncestor(tree))
        dialog.title = dialog.getText("title")
        dialog.okAction = {
            if (!validate(dialog.nameInput.text)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "NotValidName")
                alert.show()
            } else if (ResultConfigurationDAO.findByNameAndModelClassName(dialog.nameInput.text, getSelectedModel().class.name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniquesNamesRequired")
                alert.show()
            } else {
                try {

                    ResultConfiguration resultConfiguration = new ResultConfiguration(dialog.nameInput.text)
                    resultConfiguration.modelClass = getSelectedModel().class
                    resultConfiguration.save()

                    dialog.hide()

                    model.navigationTableTreeModel.addNodeForItem(resultConfiguration)
                    model.fireModelChanged()
                    resultConfiguration.load()
                    model.notifyOpenDetailView(getSelectedModel(), resultConfiguration)
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
