package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.HyperlinkEvent
import com.ulcjava.base.application.event.IHyperlinkListener
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.comment.action.FileStoreHandler
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.UserPreferences
import com.ulcjava.base.application.event.ActionEvent
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DownloadFileAction extends ResourceBasedAction {

    String fileURL
    UserPreferences userPreferences = new UserPreferences()
    ULCComponent source
    boolean open
    Log LOG = LogFactory.getLog(DownloadFileAction)

    public DownloadFileAction(String fileURL, ULCComponent source, boolean open) {
        super(open ? "DownloadFileAndOpenAction" : "DownloadFileAction")
        this.fileURL = fileURL
        this.source = source
        this.open = open
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        File file = new File(fileURL)
        if (fileURL != null && file.exists()) {
            FileChooserConfig config = new FileChooserConfig()
            config.dialogTitle = UIUtils.getText(DownloadFileAction, "saveAs")
            config.dialogType = FileChooserConfig.SAVE_DIALOG
            config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.ADD_FILE_DIR))
            config.selectedFile = file.name
            ClientContext.chooseFile([
                    onSuccess: {filePaths, fileNames ->
                        userPreferences.setUserDirectory(UserPreferences.ADD_FILE_DIR, filePaths[0])
                        File srcFile = new File(fileURL)
                        File newFile = new File(filePaths[0])
                        if (!newFile.exists() || newFile.size() != srcFile.size())
                            ClientContext.storeFile(new FileStoreHandler(fileURL, source), filePaths[0])
                        if (open)
                            ClientContext.showDocument(filePaths[0], "_new")
                    },
                    onFailure: {reason, description ->
                        if (IFileStoreHandler.CANCELLED != reason) {
                            LOG.error description
                            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(source), "importError")
                            alert.show()
                        }
                    }] as IFileChooseHandler, config, null)

        } else {
            new I18NAlert(UlcUtilities.getWindowAncestor(source), "FileNotExist", [file.name] as List).show()
        }
    }


}
