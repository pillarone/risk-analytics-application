package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.*
import groovy.util.logging.Log
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationInfoPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

import static com.ulcjava.base.shared.IDefaults.HORIZONTAL
import static org.pillarone.riskanalytics.core.simulation.SimulationState.*

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
@Log
class SimulationInfoPane {

    private SimulationState currentUISimulationState

    private ULCProgressBar progressBar
    private ULCLabel startTimeLabel
    private ULCLabel startTimeInfo
    private ULCLabel estimatedEndTimeLabel
    private ULCLabel estimatedEndTimeInfo
    private ULCLabel remainingTimeLabel
    private ULCLabel remainingTimeInfo
    private ULCBoxPane content

    @Resource
    SimulationInfoPaneModel simulationInfoPaneModel
    private final Map<SimulationState, Closure> uiStates = [:]
    private MySimulationStateChangedListener listener = new MySimulationStateChangedListener()

    @PostConstruct
    private void initialize() {
        createUiStates()
        initComponents()
        layout()
        simulationInfoPaneModel.addSimulationStateListener(listener)
    }

    @PreDestroy
    void unregister() {
        simulationInfoPaneModel.removeSimulationStateListener(listener)
    }

    private void createUiStates() {
        uiStates[NOT_RUNNING] = {
            startTimeInfo.text = "-"
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.value = 0
            progressBar.indeterminate = false
            progressBar.string = getText("SimulationNotRunningMessage")
        }

        uiStates[INITIALIZING] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = true
            progressBar.string = getText("StartSimulationMessage")
        }

        uiStates[RUNNING] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = simulationInfoPaneModel.remainingTime
            estimatedEndTimeInfo.text = simulationInfoPaneModel.estimatedEndTime
            progressBar.value = simulationInfoPaneModel.progress
            progressBar.indeterminate = false
            progressBar.string = UIUtils.getText(SimulationInfoPane, "SimulationComplete", ["${simulationInfoPaneModel.progress}"])
        }

        uiStates[POST_SIMULATION_CALCULATIONS] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = simulationInfoPaneModel.remainingTime
            estimatedEndTimeInfo.text = simulationInfoPaneModel.estimatedEndTime
            progressBar.value = simulationInfoPaneModel.progress
            progressBar.indeterminate = false
            progressBar.string = UIUtils.getText(SimulationInfoPane, "CalculatingStatistics", ["${simulationInfoPaneModel.progress}"])
        }

        uiStates[SAVING_RESULTS] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = true
            progressBar.string = getText("SavingResultsMessage")
        }

        uiStates[FINISHED] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = simulationInfoPaneModel.simulationEndTime
            progressBar.indeterminate = false
            progressBar.value = 100
            progressBar.string = getText("Done")
        }

        uiStates[CANCELED] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = "-"
            progressBar.indeterminate = false
            progressBar.value = simulationInfoPaneModel.progress
            progressBar.string = getText("Canceled")
        }

        uiStates[ERROR] = {
            startTimeInfo.text = simulationInfoPaneModel.simulationStartTime
            remainingTimeInfo.text = "-"
            estimatedEndTimeInfo.text = simulationInfoPaneModel.simulationEndTime
            progressBar.indeterminate = false
            progressBar.string = getText("Error")
        }
    }

    private String getText(String key) {
        return UIUtils.getText(SimulationInfoPane, key)
    }


    private void initComponents() {
        progressBar = new ULCProgressBar(HORIZONTAL, 0, 100)
        progressBar.stringPainted = true
        progressBar.name = "progress"

        startTimeLabel = new ULCLabel(getText("StartTime") + ":")
        startTimeInfo = new ULCLabel()
        startTimeInfo.name = "startTime"

        estimatedEndTimeLabel = new ULCLabel(getText("EstimatedEndTime") + ":")
        estimatedEndTimeInfo = new ULCLabel()
        estimatedEndTimeInfo.name = "endTime"

        remainingTimeLabel = new ULCLabel(getText("RemainingTime") + ":")
        remainingTimeInfo = new ULCLabel()
        remainingTimeInfo.name = "remainingTime"
    }

    private void layout() {
        content = new ULCBoxPane(4, 0)
        content.add(4, ULCBoxPane.BOX_EXPAND_CENTER, progressBar)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, startTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, startTimeInfo)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, estimatedEndTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, estimatedEndTimeInfo)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, remainingTimeLabel)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, remainingTimeInfo)
        content.add(ULCFiller.createHorizontalGlue())
    }

    void showAlert() {
        new ULCAlert(UlcUtilities.getWindowAncestor(content), "Error occured during simulation", I18NUtilities.getExceptionText(simulationInfoPaneModel.errorMessage), "Ok").show()
    }

    ULCBoxPane getContent() {
        return content
    }

    private class MySimulationStateChangedListener implements ISimulationStateListener {
        @Override
        void simulationStateChanged(SimulationState simulationState) {
            if (simulationState != currentUISimulationState) {
                log.info "Updating UI to ${simulationState.toString()}"
            }
            uiStates[simulationState].call()
            currentUISimulationState = simulationState
            if (currentUISimulationState == ERROR && shouldShowAlert()) {
                showAlert()
            }
        }

        private boolean shouldShowAlert() {
            boolean isOwner = (currentUser == simulationInfoPaneModel.simulationOwner)
            boolean isBatchSimulation = simulationInfoPaneModel.batchSimulation
            return isOwner && (!isBatchSimulation)
        }

        private Person getCurrentUser() {
            UserManagement.currentUser
        }
    }
}