package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.TimeCategory
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.getText

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ActionsView {

    ULCTabbedPane content
    ULCProgressBar progressBar

    ULCPollingTimer timer
    ULCLabel startTimeLabel
    ULCLabel startTimeInfo
    ULCLabel estimatedEndTimeLabel
    ULCLabel estimatedEndTimeInfo
    ULCLabel remainingTimeLabel
    ULCLabel remainingTimeInfo
    ULCButton run, stop, openResults, cancel

    ULCButton addToBatchButton
    ULCComboBox batchesComboBox
    ULCLabel actionMessage
    ULCTabbedPane buttonsTabbedPane

    int pollRepeatCount = 0

    AbstractConfigurationModel model
    EnablingActionHelper enablingActionHelper

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm")


    public ActionsView(AbstractConfigurationModel configurationModel) {
        this.model = configurationModel;
        enablingActionHelper = new EnablingActionHelper(model)
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {

        startTimeLabel = new ULCLabel(getText(this.class, "StartTime") + ":")
        startTimeInfo = new ULCLabel("-")
        remainingTimeLabel = new ULCLabel(getText(this.class, "RemainingTime") + ":")
        remainingTimeInfo = new ULCLabel("-")
        estimatedEndTimeLabel = new ULCLabel(getText(this.class, "EstimatedEndTime") + ":")
        estimatedEndTimeInfo = new ULCLabel("-")
        progressBar = new ULCProgressBar(ULCProgressBar.HORIZONTAL, 0, 100)
        progressBar.name = 'progressBar'
        progressBar.setStringPainted(true)

        timer = new ULCPollingTimer(1000, null)
        timer.repeats = true

        Closure performAction = {event ->
            pollRepeatCount++
            model.syncIterationCount()
            updateUIState()

            if (!model.simulationRunning()) {
                timer.stop()
            }
        }
        timer.addActionListener([actionPerformed: performAction] as IActionListener)

        actionMessage = new ULCLabel()


    }

    private void layoutComponents() {
        content = getButtonsTabbedPane()
    }

    private void attachListeners() {

    }

    ULCBoxPane getSimulationBottomPane() {
        run = new ULCButton(model.runAction)
        stop = new ULCButton(model.stopAction)
        openResults = new ULCButton(model.openResultAction)
        cancel = new ULCButton(model.cancelAction)

        run.name = "run"
        stop.name = "stop"
        openResults.name = "open"
        run.repaint()
        Dimension dimension = new Dimension(140, 20)
        run.setPreferredSize(dimension)
        stop.setPreferredSize(dimension)
        openResults.setPreferredSize(dimension)
        cancel.setPreferredSize(dimension)

        ULCBoxPane buttonPane = new ULCBoxPane(columns: 4, rows: 1)
        buttonPane.add(AbstractConfigurationView.spaceAround(run, 10, 2, 10, 2))
        buttonPane.add(AbstractConfigurationView.spaceAround(stop, 10, 2, 10, 2))
        buttonPane.add(AbstractConfigurationView.spaceAround(cancel, 10, 2, 10, 2))
        buttonPane.add(AbstractConfigurationView.spaceAround(openResults, 10, 2, 10, 2))
        ULCBoxPane simulationInfoSection = getSimulationInfoSectionPane()
        if (simulationInfoSection != null)
            buttonPane.add(4, ULCBoxPane.BOX_EXPAND_EXPAND, getSimulationInfoSectionPane())


        return buttonPane
    }

    public ULCBoxPane getBatchBottomPane() {
        batchesComboBox = new ULCComboBox(model.itemsComboBoxModel)
        batchesComboBox.setEditable(true)

        addToBatchButton = new ULCButton(model.addToBatchAction)
        addToBatchButton.name = "addToBatch"
        Dimension dimension = new Dimension(140, 20)
        addToBatchButton.setPreferredSize(dimension)

        ULCBoxPane buttonPane = new ULCBoxPane(columns: 3, rows: 1)
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, AbstractConfigurationView.spaceAround(batchesComboBox, 10, 10, 10, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, AbstractConfigurationView.spaceAround(addToBatchButton, 10, 8, 10, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        buttonPane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, AbstractConfigurationView.spaceAround(actionMessage, 15, 8, 10, 8))
        buttonPane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }


    ULCTabbedPane getButtonsTabbedPane() {
        buttonsTabbedPane = new ULCTabbedPane(ULCTabbedPane.TOP)
        buttonsTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        buttonsTabbedPane.addTab("Simulation", getSimulationBottomPane());
        buttonsTabbedPane.addTab("Batch", getBatchBottomPane());
        return buttonsTabbedPane
    }

    ULCBoxPane getSimulationInfoSectionPane() {
        ULCBoxPane simulationInfoSection = AbstractConfigurationView.boxLayout(getText(this.class, "SimulationProgress") + ":") {ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(4, 3, 5, 5)
            content.add(4, ULCBoxPane.BOX_EXPAND_CENTER, progressBar)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, startTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, startTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, estimatedEndTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, estimatedEndTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, remainingTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, remainingTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCFiller())
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, new ULCFiller())
            box.add(ULCBoxPane.BOX_EXPAND_EXPAND, content)
        }
        return simulationInfoSection
    }

    protected def updateUIState() {
        run.enabled = enablingActionHelper.isRunButtonEnabled()
        stop.enabled = enablingActionHelper.isStopButtonEnabled()
        cancel.enabled = enablingActionHelper.isCancelButtonEnabled()
        openResults.enabled = enablingActionHelper.isOpenResultButtonEnabled()
        addToBatchButton.enabled = run.enabled


        progressBar.enabled = model.isSimulationStopEnabled()
        progressBar.setValue(model.simulationProgress)
        String simulationMessage = ""
        String simulationMessageKey = model.simulationMessage
        if (simulationMessageKey) {
            simulationMessage = getText(this.class, simulationMessageKey)
        } else if (model.postSimulationCalculationsRunning()) {
            simulationMessage = "Calculations ${model.simulationProgress} % complete"
        } else if (model.simulationRunning()) {
            simulationMessage = "Simulation ${model.simulationProgress} % complete"
        }
        progressBar.indeterminate = model.isCurrentTaskEndIndeterminate()
        progressBar.setString(simulationMessage)

        if (model.currentSimulation != null) {
            init()
            if (model.simulationRunning()) {
                Date start = model.getSimulationStart()
                Date endTime = model.getEstimatedSimulationEnd()
                if (start != null)
                    startTimeInfo.text = dateFormat.format(start)
                if (start != null && endTime != null) {
                    use(TimeCategory) {
                        def duration = endTime - new Date()
                        remainingTimeInfo.text = "$duration.hours h $duration.minutes m $duration.seconds s"
                    }
                    estimatedEndTimeInfo.text = dateFormat.format(endTime)
                }
            } else {
                if (model.getSimulationEnd()) {
                    remainingTimeLabel.text = getText(this.class, "SimulatedIterations") + ":"
                    remainingTimeInfo.text = "${model.currentIteration}"
                    estimatedEndTimeLabel.text = getText(this.class, "EndTime") + ":"
                    estimatedEndTimeInfo.text = dateFormat.format(model.getSimulationEnd())
                }

            }
        }

    }

    protected void batchAdded(String message, boolean error) {
        actionMessage.setForeground(error ? Color.red : Color.blue)
        actionMessage.setText(message)
    }

    protected void setRemainingTime(Simulation simulation) {
        remainingTimeLabel.text = getText(this.class, "RemainingTime") + ":"
        remainingTimeInfo.text = ""
    }

    protected void startTimer() {
        timer.start()
    }

    private void init() {
        if (model.getSimulationRunner().simulationState == SimulationState.INITIALIZING) {
            model.stopAction.clicked = false
            startTimeInfo.text = ""
            estimatedEndTimeInfo.text = ""
            remainingTimeInfo.text = ""
            estimatedEndTimeInfo.text = ""
        }

    }

}
