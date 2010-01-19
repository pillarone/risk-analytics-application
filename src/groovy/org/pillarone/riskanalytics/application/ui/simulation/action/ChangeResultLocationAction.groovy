package org.pillarone.riskanalytics.application.ui.simulation.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.UserPreferences

public class ChangeResultLocationAction extends ResourceBasedAction {

    private AbstractConfigurationModel model
    UserPreferences userPreferences

    public ChangeResultLocationAction(AbstractConfigurationModel model) {
        super("ChangeResultLocation");
        this.model = model;
        enabled = false
        userPreferences = new UserPreferences()
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
                userPreferences.setUserDirectory(UserPreferences.RESULT_DIR_KEY,location)
                model.resultLocation = location
                model.outputStrategyComboBoxModel.getStrategy().resultLocation = location
                model.notifySimulationConfigurationChanged()
            },
            onFailure: {reason, description ->
            }] as IFileChooseHandler, config, ancestor)


    }


}