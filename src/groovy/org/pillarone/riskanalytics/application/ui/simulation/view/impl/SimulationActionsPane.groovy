package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationActionsPaneModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_CENTER
import static com.ulcjava.base.shared.IDefaults.BOX_LEFT_CENTER

class SimulationActionsPane implements ISimulationValidationListener {

    private ULCButton runButton
    private ULCSpinner prioritySpinner
    private ULCBoxPane content
    private ULCLabel priorityLabel
    private ULCLabel batchMessage

    private final SimulationActionsPaneModel model

    SimulationActionsPane(SimulationActionsPaneModel model) {
        this.model = model
        createComponents()
        layout()
        attachListeners()
    }

    private void attachListeners() {
        model.addPropertyChangeListener('batchMessage', new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                batchMessage.text = model.batchMessage
            }
        })
    }

    private void createComponents() {
        runButton = new ULCButton(model.runSimulationAction)
        runButton.name = "${SimulationActionsPane.simpleName}.run"
        prioritySpinner = new ULCSpinner(model.priorityModel)
        priorityLabel = new ULCLabel('Priority')
        batchMessage = new ULCLabel(model.batchMessage)
        batchMessage.foreground = Color.blue
    }

    private void layout() {
        content = new ULCBoxPane(1, 2)
        ULCBoxPane runPane = new ULCBoxPane(3, 1)
        runPane.add(BOX_LEFT_CENTER, priorityLabel)
        runPane.add(BOX_LEFT_CENTER, prioritySpinner)
        runPane.add(BOX_EXPAND_CENTER, runButton)
        runPane.border = BorderFactory.createTitledBorder(UIUtils.getText(SimulationActionsPane.class, "RunSimulation"))
        content.add(BOX_EXPAND_CENTER, runPane)
        content.add(2, BOX_EXPAND_CENTER, batchMessage)
    }

    ULCBoxPane getContent() {
        return content
    }

    @Override
    void simulationPropertyChanged(boolean isValid) {
        runButton.enabled = isValid
    }
}