package org.pillarone.riskanalytics.application.ui.simulation.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.batch.results.AbstractResultsBulkInsert
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class RunSimulationAction extends ResourceBasedAction {

    protected AbstractConfigurationModel model

    public RunSimulationAction(AbstractConfigurationModel model) {
        super("Run")
        this.model = model
        enabled = false
    }

    public RunSimulationAction(String actionName, AbstractConfigurationModel model) {
        super(actionName)
        this.model = model
    }

    public void doActionPerformed(ActionEvent event) {
        boolean isValid = validate(event)
        if (isValid)
            runSimulation()
    }

    protected boolean validate(ActionEvent event) {
        Parameterization parameterization = model.availableParameterizationVersionsForModel.selectedObject
        ResultConfiguration configuration = model.availableResultConfigurationVersionsForModel.selectedObject
        if (model.outputStrategyComboBoxModel.getStrategy() instanceof DBOutput
                && AbstractResultsBulkInsert.getBulkInsertInstance() == null) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "NoBatchInsertAllowed")
            alert.show()
            return
        }
        // TODO (Apr 23, 2009, msh): check ob setSimulationName (maybe throw exception
        if (SimulationRun?.findAllByNameAndModel(model.simulationName, model.selectedModel.name)?.find {it.toBeDeleted == false} != null) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "DuplicateName")
            alert.show()
            return
        }

        if (parameterization.changed || configuration.changed) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "UnsavedChanged")

            alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                def value = windowEvent.source.value
                if (value.equals(alert.firstButtonLabel)) {
                    if (parameterization.changed) {
                        ParameterizationDAO.withTransaction {status ->
                            parameterization.save()
                        }
                    }
                    if (configuration.changed) {
                        if (!configuration.isLoaded()) {
                            configuration.load()
                        }
                        configuration.save()
                    }
                    return true
                } else {
                    model.notifySimulationConfigurationChanged()
                }
            }] as IWindowListener)

            alert.show()
        } else {
            return true
        }
        return false
    }

    private void runSimulation() {
        int maxIterations = ApplicationHolder.application.config.maxIterations
        if (model.iterationCount > maxIterations) {
            ULCAlert alert = new MaxIterationsAlert(maxIterations)
            alert.show()
            model.iterationCount = maxIterations
        } else {
            model.runSimulation()
        }
    }

}

class MaxIterationsAlert extends I18NAlert {
    public MaxIterationsAlert(int maxIteration) {
        super("NumberOfIterationsRestricted")
        message = message.replace("__MAX_ITERATIONS__", "" + maxIteration)
    }
}
