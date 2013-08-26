package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddFileToCommentAction extends ResourceBasedAction {

    NewCommentView newCommentView
    protected UserPreferences userPreferences
    Log LOG = LogFactory.getLog(AddFileToCommentAction)

    public AddFileToCommentAction(NewCommentView newCommentView) {
        super("AddFileToCommentAction")
        this.newCommentView = newCommentView
        userPreferences = UserPreferencesFactory.getUserPreferences()
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        FileChooserConfig config = getFileChooserConfig()
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.ADD_FILE_DIR))

        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    filePaths?.each {String selectedFile ->
                        trace("Loading file $selectedFile")
                        ClientContext.loadFile(new FileLoadHandler(newCommentView), selectedFile)
                    }
                },
                onFailure: {reason, description ->
                    trace("Something went wrong: $reason, $description")
                    if (IFileLoadHandler.CANCELLED != reason) {
                        LOG.error description
                        ULCAlert alert = new I18NAlert(getAncestor(event.source as ULCComponent), "importError")
                        alert.show()
                    }
                }] as IFileChooseHandler, config, getAncestor(event.source as ULCComponent))
    }

    protected FileChooserConfig getFileChooserConfig() {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Add file to comment"
        config.dialogType = FileChooserConfig.OPEN_DIALOG
        config.setFileSelectionMode(FileChooserConfig.FILES_ONLY)
        config.setMultiSelectionEnabled(false)
        config.setAcceptAllFileFilterUsed(true)
        return config
    }

    ULCWindow getAncestor(ULCComponent source) {
        return UlcUtilities.getWindowAncestor(source)
    }


}
