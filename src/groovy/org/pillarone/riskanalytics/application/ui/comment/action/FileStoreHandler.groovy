package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.util.IFileStoreHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class FileStoreHandler implements IFileStoreHandler {
    String fileName
    ULCComponent source
    Log LOG = LogFactory.getLog(FileStoreHandler)

    public FileStoreHandler(String fileName, ULCComponent source) {
        this.fileName = fileName
        this.source = source
    }

    void prepareFile(OutputStream outputStream) {
        FileInputStream fis = new FileInputStream(fileName)
        outputStream.write fis.getBytes()
    }

    void onSuccess(String filePath, String fileName) {
        LOG.info "file ${filePath} succesfully downloaded"
    }

    void onFailure(int reason, String description) {
        if (IFileStoreHandler.CANCELLED != reason) {
            LOG.error description
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(source), "importError")
            alert.show()
        }
    }


}
