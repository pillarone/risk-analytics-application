package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

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
