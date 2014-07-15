package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ResourceClassNode
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.DefaultParameterizationDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.Resource

class CreateDefaultResourceAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateDefaultResourceAction)

    CreateDefaultResourceAction(ULCTableTree tree) {
        super("CreateDefaultResource", tree)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        ResourceClassNode node = tree.selectedPath.lastPathComponent

        DefaultParameterizationDialog dialog = new DefaultParameterizationDialog(UlcUtilities.getWindowAncestor(tree), true)
        dialog.title = "New ${node.resourceClass.simpleName}"
        dialog.okAction = {
            if (!validate(dialog.nameInput.text)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "NotValidName")
                alert.show()
            } else if (ResourceDAO.findByNameAndResourceClassName(dialog.nameInput.text, node.resourceClass.name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniquesNamesRequired")
                alert.show()
            } else {
                try {
                    Resource resource = null
                    ParameterizationDAO.withTransaction { status ->
                        resource = ParameterizationHelper.createDefaultResource(dialog.nameInput.text, node.resourceClass.newInstance())
                        resource.save()
                        resource = ModellingItemFactory.getResourceFromDAO(resource.dao)
                        resource.load()
                    }
                    dialog.hide()
                    riskAnalyticsEventBus.post(new OpenDetailViewEvent(new ResourceUIItem(resource)))
                } catch (Exception ex) {
                    LOG.error("Error creating new resource", ex)
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
        if (seps.any { name.indexOf(it) != -1 }) return false
        return true
    }
}
