package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.util.IFileLoadHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
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
            String dir = ApplicationHolder.getApplication().getConfig().getProperty("comment_file_dir")
            names.eachWithIndex {String fileName, int index ->
                File file = new File(dir + "/" + fileName)
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
}
