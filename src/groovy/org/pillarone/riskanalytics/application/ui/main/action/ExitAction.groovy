package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.core.output.DeleteSimulationService
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ExitAction extends ResourceBasedAction {

    public ExitAction() {
        super("Exit")
    }

    public void doActionPerformed(ActionEvent event) {
        terminate()
    }

    public static void terminate(){
        // todo dk: move this into a job 
        DeleteSimulationService deleteSimulationService = (DeleteSimulationService) ApplicationHolder.application.mainContext.getBean('deleteSimulationService')
        if(deleteSimulationService != null){
            deleteSimulationService.deleteAllMarkedSimulations()
        }
        ApplicationContext.terminate();
    }
}
