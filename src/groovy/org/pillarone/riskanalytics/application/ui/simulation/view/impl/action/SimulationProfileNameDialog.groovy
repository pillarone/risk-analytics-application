package org.pillarone.riskanalytics.application.ui.simulation.view.impl.action

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

import static org.pillarone.riskanalytics.application.util.LocaleResources.getString

public class SimulationProfileNameDialog {

    private static final String SAVE_SIMULATION_PROFILE_DIALOG = 'SaveSimulationProfileDialog'
    private static final String TITLE = 'title'
    private static final String NAME = 'name'
    private static final String OK_BUTTON = 'okButton'
    private static final String CANCEL_BUTTON = 'cancelButton'
    private static final String NOT_ALLOWED_TO_SAVE_SIMULATION_PROFILE = 'NotAllowedToSaveSimulationProfile'
    private static final String UNIQUE_NAME_FOR_SIMULATION_PROFILE = 'UniqueNameForSimulationProfile'
    private static final String FAILED_TO_SAVE_SIMULATION_PROFILE = 'FailedToSaveSimulationProfile'

    private ULCTextField nameInput
    private ULCButton okButton
    private ULCButton cancelButton
    private ULCDialog dialog
    private final Closure okAction
    private final ULCRootPane parent
    private final Class modelClass
    private final String currentUsername

    SimulationProfileNameDialog(ULCRootPane parent, Closure okAction, Class modelClass, String currentUsername) {
        this.modelClass = modelClass
        this.parent = parent
        this.okAction = okAction
        initComponents()
        layout()
        bind()
        this.currentUsername = currentUsername
    }

    void show() {
        dialog.visible = true
    }

    void setName(String name) {
        nameInput.text = name
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = SAVE_SIMULATION_PROFILE_DIALOG
        dialog.title = getText(TITLE)
        nameInput = new ULCTextField()
        nameInput.name = NAME
        okButton = new ULCButton(getText(OK_BUTTON))
        okButton.name = OK_BUTTON
        cancelButton = new ULCButton(getText(CANCEL_BUTTON))
        cancelButton.name = CANCEL_BUTTON
    }

    private void layout() {
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("${getText(NAME)}:"))
        nameInput.preferredSize = new Dimension(200, 20)
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, nameInput)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
        okButton.preferredSize = new Dimension(120, 20)
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, okButton)
        cancelButton.preferredSize = new Dimension(120, 20)
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, cancelButton)
        dialog.add(content)
        dialog.locationRelativeTo = parent
        dialog.pack()
        dialog.resizable = false
    }

    private void bind() {
        IActionListener saveActionListener = createSaveActionListener()
        nameInput.addActionListener(saveActionListener)
        okButton.addActionListener(saveActionListener)
        cancelButton.addActionListener({ e -> hide() } as IActionListener)
    }

    private IActionListener createSaveActionListener() {
        { ActionEvent actionEvent ->
            if (empty) {
                return
            }
            if (!allowed) {
                showNotAllowedAlert()
                return
            }
            if (existent) {
                showChooseToOverwriteAlert()
                return
            }
            callOkAction()
        } as IActionListener
    }

    private void showNotAllowedAlert() {
        new I18NAlert(parent, NOT_ALLOWED_TO_SAVE_SIMULATION_PROFILE).show()
    }

    private void showChooseToOverwriteAlert() {
        I18NAlert alert = new I18NAlert(parent, UNIQUE_NAME_FOR_SIMULATION_PROFILE)
        alert.show()
        alert.addWindowListener(new IWindowListener() {
            @Override
            void windowClosing(WindowEvent event) {
                if (alert.value.equals(alert.firstButtonLabel)) {
                    callOkAction()
                }
            }
        })
    }

    private void callOkAction() {
        if (okAction.call(nameInput.text)) {
            hide()
        } else {
            new I18NAlert(FAILED_TO_SAVE_SIMULATION_PROFILE).show()
        }
    }

    private boolean isExistent() {
        SimulationProfile.exists(nameInput.text, modelClass)
    }

    private boolean isAllowed() {
        def creator = SimulationProfile.getCreator(nameInput.text, modelClass)
        creator ? currentUsername == creator.username : true
    }

    private boolean isEmpty() {
        !nameInput.text
    }

    private void hide() {
        dialog.visible = false
    }

    private String getText(String key) {
        return getString("${SAVE_SIMULATION_PROFILE_DIALOG}.${key}")
    }
}