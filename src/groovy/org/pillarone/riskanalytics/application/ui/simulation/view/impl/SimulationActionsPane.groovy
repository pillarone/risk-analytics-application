package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.logging.view.RealTimeLoggingView
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround
import static org.pillarone.riskanalytics.core.simulation.SimulationState.*

/**
 * A view which can be used to run & monitor a simulation (provided by an ISimulationProvider in the model).
 * A polling timer is used to regularly update the UI.
 */
class SimulationActionsPane implements IActionListener, ISimulationListener, ISimulationValidationListener {

    private static final Log LOG = LogFactory.getLog(SimulationActionsPane)

    ULCTabbedPane content
    SimulationState currentUISimulationState

    private ULCProgressBar progressBar
    private ULCPollingTimer timer
    private ULCLabel startTimeLabel
    private ULCLabel startTimeInfo
    private ULCLabel estimatedEndTimeLabel
    private ULCLabel estimatedEndTimeInfo
    private ULCLabel remainingTimeLabel
    private ULCLabel remainingTimeInfo
    private ULCButton run, openResults, cancel

    private ULCComboBox availableBatchRuns
    private ULCButton addToBatch
    private ULCLabel batchMessage

    private RealTimeLoggingView loggingView

    SimulationActionsPaneModel model
    private Map<SimulationState, Closure> uiStates = [:]

    private boolean configurationValid = false

    public SimulationActionsPane(SimulationActionsPaneModel model) {
        this.model = model;
        model.addSimulationListener(this)

        //The state of the UI is defined through the current simulation state.
        //There is a closure for each state, which will update the ui to that state.
        //after each polling timer event, the closure of the current simulation state is called
        uiStates.put(NOT_RUNNING, {
            startTimeInfo.text = "-"
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.value = 0
            progressBar.indeterminate = false
            progressBar.string = model.getText("SimulationNotRunningMessage")
            run.enabled = configurationValid
            addToBatch.enabled = configurationValid
            cancel.enabled = false
            openResults.enabled = false
        })

        uiStates.put(INITIALIZING, {
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = true
            progressBar.string = model.getText("StartSimulationMessage")
            run.enabled = false
            addToBatch.enabled = false
            cancel.enabled = true
            openResults.enabled = false
        })

        uiStates.put(RUNNING, {
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = model.remainingTime
            estimatedEndTimeInfo.text = model.estimatedEndTime
            progressBar.value = model.progress
            progressBar.indeterminate = false
            progressBar.string = UIUtils.getText(SimulationActionsPane.class, "SimulationComplete", ["${model.progress}"])
            run.enabled = false
            addToBatch.enabled = false
            cancel.enabled = true
            openResults.enabled = false
        })

        uiStates.put(POST_SIMULATION_CALCULATIONS, {
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = model.remainingTime
            estimatedEndTimeInfo.text = model.estimatedEndTime
            progressBar.value = model.progress
            progressBar.indeterminate = false
            progressBar.string = UIUtils.getText(SimulationActionsPane.class, "CalculatingStatistics", ["${model.progress}"])
            run.enabled = false
            addToBatch.enabled = false
            cancel.enabled = true
            openResults.enabled = false
        })

        uiStates.put(SAVING_RESULTS, {
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = true
            progressBar.string = model.getText("SavingResultsMessage")
            run.enabled = false
            addToBatch.enabled = false
            cancel.enabled = false
            openResults.enabled = false
        })

        uiStates.put(FINISHED, {
            timer.stop()
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = model.simulationEndTime
            progressBar.indeterminate = false
            progressBar.value = 100
            progressBar.string = model.getText("Done")
            run.enabled = configurationValid
            addToBatch.enabled = configurationValid
            cancel.enabled = false
            openResults.enabled = true
        })

        uiStates.put(CANCELED, {
            timer.stop()
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = false
            progressBar.value = model.progress
            progressBar.string = model.getText("Canceled")
            run.enabled = configurationValid
            addToBatch.enabled = configurationValid
            cancel.enabled = false
            openResults.enabled = false
        })

        uiStates.put(ERROR, {
            timer.stop()
            startTimeInfo.text = model.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = model.simulationEndTime
            progressBar.indeterminate = false
            progressBar.string = model.getText("Error")
            run.enabled = configurationValid
            addToBatch.enabled = configurationValid
            cancel.enabled = false
            openResults.enabled = false
        })

        initComponents()
        layoutComponents()
        updateUIState(NOT_RUNNING)
    }

    void layoutComponents() {
        ULCBoxPane innerPane = new ULCBoxPane()
        innerPane.border = BorderFactory.createTitledBorder(model.getText("SimulationProgress") + ":")
        ULCBoxPane infoPane = new ULCBoxPane(4, 0)
        infoPane.add(4, ULCBoxPane.BOX_EXPAND_CENTER, progressBar)
        infoPane.add(ULCBoxPane.BOX_LEFT_CENTER, startTimeLabel)
        infoPane.add(ULCBoxPane.BOX_EXPAND_CENTER, startTimeInfo)
        infoPane.add(ULCBoxPane.BOX_LEFT_CENTER, estimatedEndTimeLabel)
        infoPane.add(ULCBoxPane.BOX_EXPAND_CENTER, estimatedEndTimeInfo)
        infoPane.add(ULCBoxPane.BOX_LEFT_CENTER, remainingTimeLabel)
        infoPane.add(ULCBoxPane.BOX_EXPAND_CENTER, remainingTimeInfo)
        infoPane.add(ULCFiller.createHorizontalGlue())

        innerPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, infoPane)
        ULCBoxPane simulationContent = new ULCBoxPane(3, 0)
        simulationContent.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(run, 10, 2, 10, 2, ULCBoxPane.BOX_EXPAND_CENTER))
        simulationContent.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(cancel, 10, 2, 10, 2, ULCBoxPane.BOX_EXPAND_CENTER))
        simulationContent.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(openResults, 10, 2, 10, 2, ULCBoxPane.BOX_EXPAND_CENTER))
        simulationContent.add(4, ULCBoxPane.BOX_EXPAND_EXPAND, innerPane)

        content.addTab("Simulation", simulationContent)

        innerPane = new ULCBoxPane(3, 2)
        innerPane.border = BorderFactory.createTitledBorder(UIUtils.getText(SimulationActionsPane.class, "AddToBatch"))
        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(availableBatchRuns, 0, 5, 0, 0))
        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, addToBatch)
        innerPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())

        innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, batchMessage)
        innerPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())

        ULCBoxPane batchPane = new ULCBoxPane(1, 0)
        batchPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, innerPane)

        content.addTab("Batch", batchPane)
        loggingView = new RealTimeLoggingView()
        content.addTab("Logging", loggingView.getContent())
    }

    void initComponents() {
        content = new ULCTabbedPane()
        progressBar = new ULCProgressBar(ULCProgressBar.HORIZONTAL, 0, 100)
        progressBar.stringPainted = true
        progressBar.name = "progress"

        timer = new ULCPollingTimer(1000, this)
        timer.syncClientState = false
        timer.repeats = true

        startTimeLabel = new ULCLabel(model.getText("StartTime") + ":")
        startTimeInfo = new ULCLabel()
        startTimeInfo.name = "startTime"
        estimatedEndTimeLabel = new ULCLabel(model.getText("EstimatedEndTime") + ":")
        estimatedEndTimeInfo = new ULCLabel()
        estimatedEndTimeInfo.name = "endTime"
        remainingTimeLabel = new ULCLabel(model.getText("RemainingTime") + ":")
        remainingTimeInfo = new ULCLabel()
        remainingTimeInfo.name = "remainingTime"

        run = new ULCButton(model.runSimulationAction)
        run.name = "${SimulationActionsPane.getSimpleName()}.run"
        cancel = new ULCButton(model.cancelSimulationAction)
        cancel.name = "cancel"
        openResults = new ULCButton(model.openResultsAction)
        openResults.name = "${SimulationActionsPane.getSimpleName()}.openResults"

        availableBatchRuns = new ULCComboBox(model.batchRunComboBoxModel)
        availableBatchRuns.editable = true
        addToBatch = new ULCButton(model.addToBatchAction)
        batchMessage = new ULCLabel(model.batchMessage)
        batchMessage.foreground = Color.blue
    }

    /**
     * is called when the polling timer event fires.
     */
    void actionPerformed(ActionEvent actionEvent) {
        SimulationState simulationState = model.simulationState
        updateUIState(simulationState)
        if (simulationState == FINISHED || simulationState == CANCELED || simulationState == ERROR) {
            model.notifySimulationStop()
        }
    }

    protected void updateUIState(SimulationState simulationState) {
        if (LOG.infoEnabled && simulationState != currentUISimulationState) {
            LOG.info "Updating UI to ${simulationState.toString()}"
        }
        uiStates[simulationState].call()
        currentUISimulationState = simulationState
    }

    void simulationEnd(Simulation simulation, Model model) {
        //the polling timer is stopped in the ui state closures to make sure that the last state is correctly applied
        if (currentUISimulationState == ERROR) {
            ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(content), "Error occured during simulation", I18NUtilities.getExceptionText(this.model.errorMessage), "Ok")
            alert.show()
        }
        loggingView.model.stop()
        this.model.handler = null
    }

    void simulationStart(Simulation simulation) {
        timer.start()
        loggingView.model.start()
    }

    void simulationToBatchAdded() {
        batchMessage.text = model.batchMessage
    }

    /**
     *  is called when the current simulation configuration changes.
     * is used to disable the run button when the new config is invalid
     */
    void simulationPropertyChanged(boolean isValid) {
        configurationValid = isValid
        currentUISimulationState = NOT_RUNNING
        updateUIState(currentUISimulationState)
    }


}
