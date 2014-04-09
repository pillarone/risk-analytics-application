package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemUtils
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RunSimulationHandler {

    protected SimulationActionsPaneModel model

    public RunSimulationHandler(SimulationActionsPaneModel model) {
        this.model = model
    }

    private void saveAndRun() {
        Parameterization parameterization = model.simulation.parameterization
        ResultConfiguration configuration = model.simulation.template
        if (parameterization.changed) {
            parameterization.save()
        }
        if (configuration.changed) {
            if (!configuration.isLoaded()) {
                configuration.load()
            }
            configuration.save()
        }
        runSimulation()
    }

    /**
     * the changed item is already used in a simulation. There are two possibilities to run a simulation:
     * delete all depending results or create a new version of changed item
     * @param windowEvent
     * @return
     */
    protected void handleUnsavedUsedItem(WindowEvent windowEvent, ULCAlert alert, def parent) {
        def value = windowEvent.source.value
        Model itemModel = getItemModel(model.simulation.parameterization)
        List items = [model.simulation.parameterization, model.simulation.template]
        if (value.equals(alert.firstButtonLabel)) {
            //delete all runs and run
            if (deleteDependingResultsAndSave(itemModel, items)) {
                model.runSimulation()
            } else {
                new I18NAlert(UlcUtilities.getWindowAncestor(parent), "DeleteAllDependentRunsError").show()
            }

        } else if (value.equals(alert.secondButtonLabel)) {
            //create a new version
            List<ModellingItem> newItems = createNewVersion(itemModel, items)
            if (newItems && newItems.size() == 2) {
                model.simulation.parameterization = newItems[0]
                model.simulation.template = newItems[1]
                model.runSimulation()
            }

        }
    }

    /**
     * to run a simulation after saving the changed item
     * @param windowEvent
     * @param alert
     * @return
     */
    protected void handleUnsavedItem(WindowEvent windowEvent, ULCAlert alert) {
        def value = windowEvent.source.value
        if (value.equals(alert.firstButtonLabel)) {
            saveAndRun()
        }
    }

    private void runSimulation() {
        model.runSimulation()
    }

    /**
     * look up after running a simulation with the same item
     * @param item
     * @return
     */
    private boolean isRunning(ModellingItem item) {
        List<SimulationRun> simulationRuns = item.simulations;
        //check if at least one simulation is running
        for (SimulationRun simulationRun: simulationRuns) {
            if (!simulationRun.endTime) return true
        }
        return false
    }

    protected boolean deleteDependingResultsAndSave(Model itemModel, List<ModellingItem> items) {
        boolean status = true
        for (ModellingItem item: items) {
            if (item.changed && isRunning(item))
                status = true
        }
        if (!status) return status
        SimulationRun.withTransaction {TransactionStatus transactionStatus ->
            for (ModellingItem item: items) {
                if (item.changed && status) {
                    status = UIItemUtils.deleteDependingResults(model.mainModel, itemModel, item)
                }
            }
            if (status) {
                for (ModellingItem item: items) {
                    if (item.changed) item.save()
                }
            }
        }

        return status
    }

    /**
     * create a new version of changed items
     * @param itemModel
     * @param items list of modellingItem, the first item is p14n and the second configuration
     * @return
     */
    protected List<ModellingItem> createNewVersion(Model itemModel, List<ModellingItem> items) {
        List<ModellingItem> newItems = []
        SimulationRun.withTransaction {TransactionStatus transactionStatus ->
            for (ModellingItem item: items) {
                if (item.changed) {
                    item.load()
                    ModellingUIItem modellingUIItem = model.mainModel.getAbstractUIItem(item)
                    if (modellingUIItem instanceof ParameterizationUIItem) { //TODO: find a way to show new version comment dialog
                        newItems << modellingUIItem.createNewVersion(itemModel, "", false).item
                    } else {
                        newItems << modellingUIItem.createNewVersion(itemModel, false).item
                    }
                    model.mainModel.closeItem(itemModel, modellingUIItem)
                } else
                    newItems << item
            }
        }
        return newItems
    }

    private Model getItemModel(ModellingItem item) {
        Model itemModel = item.modelClass.newInstance()
        itemModel.init()
        return itemModel
    }
}
