package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelImportHandler
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences

class ImportParameterizationExcelAction extends ImportAction {
    ImportParameterizationExcelAction(ULCTableTree tree, RiskAnalyticsMainModel model, String name) {
        super(tree, model, name)
    }

    protected void importItem(def node) {
        ancestor?.cursor = Cursor.WAIT_CURSOR

        FileChooserConfig config = getFileChooserConfig(node)
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.IMPORT_DIR_KEY))

        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    filePaths?.each {def selectedFile ->
                        ClientContext.loadFile(new ExcelImportHandler(), selectedFile)
                    }
                    return
                },
                onFailure: {reason, description ->
                    if (IFileLoadHandler.CANCELLED != reason) {
                        LOG.error description
                        ULCAlert alert = new I18NAlert(ancestor, "importError")
                        alert.show()
                    }
                    ancestor?.cursor = Cursor.DEFAULT_CURSOR
                }] as IFileChooseHandler, config, ancestor)
    }

    @Override
    String getExtension() {
        'xlsx'
    }
}
