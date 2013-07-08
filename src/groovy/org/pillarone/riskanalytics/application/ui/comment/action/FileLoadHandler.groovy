package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.util.IFileLoadHandler
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.FileConstants;
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class FileLoadHandler implements IFileLoadHandler {
    NewCommentView newCommentView
    Log LOG = LogFactory.getLog(AddFileToCommentAction)

    public FileLoadHandler(NewCommentView newCommentView) {
        this.newCommentView = newCommentView
    }

    void onSuccess(InputStream[] inputStreams, String[] paths, String[] names) {
        ExceptionSafe.protect {
            UserPreferencesFactory.getUserPreferences().setUserDirectory(UserPreferences.ADD_FILE_DIR, paths[0])
            names.eachWithIndex { String fileName, int index ->
                File file = File.createTempFile(fileName, '',new File(FileConstants.TEMP_FILE_DIRECTORY))
                file.bytes = inputStreams[index].bytes
                newCommentView.fileAdded(new CommentFile(fileName,file))
            }
        }

    }

    void onFailure(int reason, String description) {
        if (IFileLoadHandler.CANCELLED != reason) {
            LOG.error description
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(newCommentView.content), "importError")
            alert.show()
        }
    }

    private String getNewFileName(String dir, String fileName) {
        GString suffix = "_${new Date().getTime()}"
        if (fileName.lastIndexOf(".") > 0) {
            String name = fileName.substring(0, fileName.lastIndexOf("."))
            String ext = fileName.substring(fileName.lastIndexOf("."))
            fileName = name + suffix + ext
        } else
            fileName = fileName + suffix
        return fileName
    }
}
