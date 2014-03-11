package org.pillarone.riskanalytics.application.ui.simulation.view.impl.action

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationProfileActionsPane
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class SaveSimulationProfileAction extends ResourceBasedAction {


    public static final String SAVE_SIMULATION_PROFILE = 'SaveSimulationProfile'

    SimulationProfileActionsPane simulationProfileActionsPane
    ProfileNameDialog profileNameDialog

    SaveSimulationProfileAction(SimulationProfileActionsPane simulationProfileActionsPane) {
        super(SAVE_SIMULATION_PROFILE)
        this.simulationProfileActionsPane = simulationProfileActionsPane
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        if (profileNameDialog == null) {
            profileNameDialog = new ProfileNameDialog(UlcUtilities.getRootPane(simulationProfileActionsPane.content), { String name ->
                simulationProfileActionsPane.model.saveCurrentProfile(name)
            })
        }
        profileNameDialog.nameInput.text = simulationProfileActionsPane.model.simulationProfiles.selectedItem
        profileNameDialog.show()
    }
}

class ProfileNameDialog {

    private ULCDialog dialog
    ULCTextField nameInput
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction
    private final ULCRootPane parent

    ProfileNameDialog(ULCRootPane parent, Closure okAction) {
        this.parent = parent
        this.okAction = okAction
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'saveSimulationProfileDialog'
        dialog.title = getText('title')
        nameInput = new ULCTextField()
        nameInput.name = 'newName'
        okButton = new ULCButton(getText("okButton"))
        okButton.name = 'okButton'
        cancelButton = new ULCButton(getText("cancelButton"))

    }

    private void layoutComponents() {
        nameInput.preferredSize = new Dimension(200, 20)
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("name") + ":"))
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

    private void attachListeners() {
        IActionListener action = [actionPerformed: { e ->
            if (valid) {
                okAction.call(nameInput.text); hide()
            } else {
                I18NAlert alert = new I18NAlert(parent, "UniqueNameForSimulationProfile")
                alert.show()
                alert.addWindowListener(new IWindowListener() {
                    @Override
                    void windowClosing(WindowEvent event) {
                        if (alert.value.equals(alert.firstButtonLabel)) {
                            okAction.call(nameInput.text); hide()
                        }
                    }
                })
            }
        }] as IActionListener

        nameInput.addActionListener(action)
        okButton.addActionListener(action)
        cancelButton.addActionListener([actionPerformed: { e -> hide() }] as IActionListener)
    }

    protected boolean isValid() {
        def text = nameInput.text
        !(text.empty || SimulationProfile.exists(text))
    }


    public void show() {
        dialog.visible = true
    }

    public hide() {
        dialog.visible = false
    }

    public String getText(String key) {
        return LocaleResources.getString("SaveSimulationProfileDialog." + key);
    }
}

