package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelExportHandler
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences

class ExportParameterizationExcelAction extends ExportAction {

    ExportParameterizationExcelAction(ULCTableTree tree, RiskAnalyticsMainModel model, String title) {
        super(tree, model, title)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Export"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.fileSelectionMode = FileChooserConfig.FILES_ONLY
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.EXPORT_DIR_KEY))
        config.selectedFile = "${selectedModel.class.simpleName}-Parameterization-Template.xlsx"
        ExcelExportHandler handler = new ExcelExportHandler(selectedModel)
        ClientContext.chooseFile([
                onSuccess: { String[] filePaths, fileNames ->
                    ClientContext.storeFile([prepareFile: { OutputStream stream ->
                        stream.write(handler.exportModel())
                    }, onSuccess                        : { path, name ->
                    }, onFailure                        : { reason, description ->
                        if (reason == IFileStoreHandler.FAILED) {
                            LOG.error description
                            showAlert("exportError")
                        }
                    }] as IFileStoreHandler, filePaths[0], Long.MAX_VALUE, false)


                },
                onFailure: { reason, description ->
                    if (reason != IFileChooseHandler.CANCELLED) {
                        LOG.error description
                        showAlert("exportError")
                    }
                }] as IFileChooseHandler, config, ancestor)

    }
}
