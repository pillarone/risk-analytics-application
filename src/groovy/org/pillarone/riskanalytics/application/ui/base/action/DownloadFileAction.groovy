package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import groovy.transform.CompileStatic
import net.sf.jmimemagic.Magic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.document.ShowDocumentStrategyFactory
import org.pillarone.riskanalytics.application.ui.comment.action.FileStoreHandler
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class DownloadFileAction extends ResourceBasedAction {

    UserPreferences userPreferences = UserPreferencesFactory.getUserPreferences()
    ULCComponent source
    boolean open
    CommentFile commentFile
    Log LOG = LogFactory.getLog(DownloadFileAction)

    public DownloadFileAction(CommentFile commentFile, ULCComponent source, boolean open) {
        super(open ? "DownloadFileAndOpenAction" : "DownloadFileAction")
        this.commentFile = commentFile
        this.source = source
        this.open = open
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        byte[] content = commentFile.content
        if (open) {
            ShowDocumentStrategyFactory.getInstance().showDocument(commentFile.filename, content, Magic.getMagicMatch(content, true).getMimeType())
            return
        }
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = UIUtils.getText(DownloadFileAction, "saveAs")
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.ADD_FILE_DIR))
        config.selectedFile = commentFile.filename
        ClientContext.chooseFile([
                onSuccess: { String[] filePaths, fileNames ->
                    userPreferences.setUserDirectory(UserPreferences.ADD_FILE_DIR, filePaths[0])
                    ClientContext.storeFile(new FileStoreHandler(content, source), filePaths[0], Long.MAX_VALUE, true)
                },
                onFailure: { reason, description ->
                    if (IFileStoreHandler.CANCELLED != reason) {
                        LOG.error description
                        ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(source), "importError")
                        alert.show()
                    }
                }] as IFileChooseHandler, config, null)
    }
}