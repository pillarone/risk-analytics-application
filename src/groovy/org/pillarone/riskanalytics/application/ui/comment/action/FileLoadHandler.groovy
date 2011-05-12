package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.util.IFileLoadHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.FileConstants;
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.UserPreferences

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class FileLoadHandler implements IFileLoadHandler {
    NewCommentView newCommentView
    Log LOG = LogFactory.getLog(AddFileToCommentAction)

    public FileLoadHandler(NewCommentView newCommentView) {
        this.newCommentView = newCommentView
    }

    void onSuccess(InputStream[] inputStreams, String[] paths, String[] names) {
        ExceptionSafe.protect {
            new UserPreferences().setUserDirectory(UserPreferences.ADD_FILE_DIR, paths[0])
            String dir = FileConstants.COMMENT_FILE_DIRECTORY
            names.eachWithIndex {String fileName, int index ->
                File file = new File(dir + File.separator + fileName)
                if (file.exists()) {
                    fileName = getNewFileName(dir, fileName)
                    file = new File(dir + File.separator + fileName)
                }

                FileOutputStream outputStream = new FileOutputStream(file)
                outputStream.write(inputStreams[index].getBytes())
                newCommentView.fileAdded(fileName)
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
