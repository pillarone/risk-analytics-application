package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.core.output.DeleteSimulationService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.UserContext
import com.ulcjava.base.application.ClientContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class ExitAction extends ResourceBasedAction {

    Log LOG = LogFactory.getLog(ExitAction)

    public ExitAction() {
        super("Exit")
    }

    public void doActionPerformed(ActionEvent event) {
        if (UserContext.isApplet()) {
            logout()
        } else
            terminate()
    }

    public static void terminate() {
        // todo dk: move this into a job
        DeleteSimulationService deleteSimulationService = (DeleteSimulationService) ApplicationHolder.application.mainContext.getBean('deleteSimulationService')
        if (deleteSimulationService != null) {
            deleteSimulationService.deleteAllMarkedSimulations()
        }
        ApplicationContext.terminate();
    }

    public void logout() {
        String url = null
        try {
            url = UserContext.getBaseUrl() + "/logout"
            ClientContext.showDocument(url, "_self")
        } catch (Exception ex) {
            LOG.error("Logout error by calling $url : $ex")
        } finally {
            terminate()
        }
    }
}
