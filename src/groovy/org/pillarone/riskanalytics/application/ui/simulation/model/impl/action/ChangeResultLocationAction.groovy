package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory

public class ChangeResultLocationAction extends ResourceBasedAction {

    private SimulationSettingsPaneModel model
    private UserPreferences userPreferences
    private Closure action

    public ChangeResultLocationAction(SimulationSettingsPaneModel model, Closure action) {
        super("ChangeResultLocation");
        this.model = model;
        this.action = action
        enabled = false
        userPreferences = UserPreferencesFactory.getUserPreferences()
    }

    public void doActionPerformed(ActionEvent event) {
        ULCWindow ancestor = UlcUtilities.getWindowAncestor(event.source)

        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = UIUtils.getText(this.class, "Destination")
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.fileSelectionMode = FileChooserConfig.DIRECTORIES_ONLY
        config.setAcceptAllFileFilterUsed(false)
        config.selectedFile = model.resultLocation
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.RESULT_DIR_KEY))

        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String location = filePaths[0]
                    userPreferences.setUserDirectory(UserPreferences.RESULT_DIR_KEY, location)
                    model.resultLocation = location
                    model.outputStrategies.getStrategy().resultLocation = location
                    action.call()
                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)


    }


}