package org.pillarone.riskanalytics.application.ui.base.view

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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DownloadFilePane extends AbstractLinkPane {

    Log LOG = LogFactory.getLog(DownloadFilePane)
    ULCComponent source

    @Override
    void addListener() {
        addHyperlinkListener(new DownloadFile())
    }

    class DownloadFile implements IHyperlinkListener {
        UserPreferences userPreferences = new UserPreferences()

        void linkActivated(HyperlinkEvent hyperlinkEvent) {
            String url = null
            try {
                url = hyperlinkEvent.getURL().toExternalForm()
            } catch (NullPointerException ex) {
                url = hyperlinkEvent.getDescription()
            }
            if (url != null) {
                File file = new File(url)
                FileChooserConfig config = new FileChooserConfig()
                config.dialogTitle = UIUtils.getText(DownloadFilePane, "saveAs")
                config.dialogType = FileChooserConfig.SAVE_DIALOG
                config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.ADD_FILE_DIR))
                config.selectedFile = file.name
                ClientContext.chooseFile([
                        onSuccess: {filePaths, fileNames ->
                            userPreferences.setUserDirectory(UserPreferences.ADD_FILE_DIR, filePaths[0])
                            ClientContext.storeFile(new FileStoreHandler(url, source), filePaths[0])
                        },
                        onFailure: {reason, description ->
                            if (IFileStoreHandler.CANCELLED != reason) {
                                LOG.error description
                                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(source), "importError")
                                alert.show()
                            }
                        }] as IFileChooseHandler, config, null)

            }
        }

        void linkError(HyperlinkEvent hyperlinkEvent) {
        }

    }


}
