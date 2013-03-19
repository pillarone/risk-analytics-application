package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class RunSimulationAction extends ResourceBasedAction {

    protected SimulationActionsPaneModel model
    private RunSimulationHandler handler

    public RunSimulationAction(SimulationActionsPaneModel model) {
        this("Run", model)
    }

    public RunSimulationAction(String actionName, SimulationActionsPaneModel model) {
        super(actionName)
        this.model = model
        this.handler = new RunSimulationHandler(model)
        enabled = false
    }

    public void doActionPerformed(ActionEvent event) {
        boolean isValid = validate(event)
        if (isValid) {
            model.runSimulation()
        }
    }

    protected boolean validate(ActionEvent event) {
        model.simulation = model.simulationProvider.simulation
        model.outputStrategy = model.simulationProvider.outputStrategy

        Parameterization parameterization = model.simulation.parameterization
        ResultConfiguration configuration = model.simulation.template

        if (SimulationRun?.findAllByNameAndModel(model.simulation.name, model.simulation.modelClass.name)?.find {(!it.toBeDeleted)} != null) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "DuplicateName")
            alert.show()
            return
        }

        if (parameterization.changed || configuration.changed) {
            if (isUsedInSimulation(parameterization) || isUsedInSimulation(configuration)) {
                //unsaved used item
                ULCAlert alert = new I18NAlert("UnsavedUsedItem")
                alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                    handler.handleUnsavedUsedItem(windowEvent, alert, event.source)
                }] as IWindowListener)
                alert.show()
            } else {
                //unsaved unused item
                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "UnsavedChanged")
                alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                    handler.handleUnsavedItem(windowEvent, alert)
                }] as IWindowListener)
                alert.show()
            }

        } else {
            return true
        }
        return false
    }

    private boolean isUsedInSimulation(ModellingItem item) {
        return item.changed && item.isUsedInSimulation()
    }

}
