package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelImportHandler
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ImportResult
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

@CompileStatic
class ImportParameterizationExcelAction extends ImportAction {
    private static final int ALERT_ROW_SIZE = 20

    ImportParameterizationExcelAction(ULCTableTree tree, RiskAnalyticsMainModel model, String name) {
        super(tree, model, name)
    }

    @Override
    protected doAction(ITableTreeNode node) {
        internalImportItem(node)
    }

    @Override
    protected doAction(ItemGroupNode node) {
        internalImportItem(node)
    }

    private internalImportItem(ITableTreeNode node) {
        ancestor?.cursor = Cursor.WAIT_CURSOR

        FileChooserConfig config = getFileChooserConfig(node)
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.IMPORT_DIR_KEY))

        ClientContext.chooseFile([
                onSuccess: { String[] filePaths, String[] fileNames ->
                    filePaths.each { String selectedFile ->
                        ExcelImportHandler handler = new ExcelImportHandler()
                        ClientContext.loadFile([onSuccess: { InputStream[] ins, String[] paths, String[] filenames ->
                            handler.loadWorkbook(ins[0], filenames[0])
                            if (selectedUIItem instanceof ParameterizationUIItem) {
                                handler.setParameterizationOnModel(selectedModel, selectedUIItem.item as Parameterization)
                            }
                            List<ImportResult> validationResult = handler.validate(selectedModel)
                            if (validationResult.any { ImportResult res -> res.type == ImportResult.Type.ERROR }) {
                                ULCAlert alert = new I18NAlert(ancestor, "excelImportError", [filenames[0], formatValidationResult(validationResult.findAll { ImportResult res -> res.type == ImportResult.Type.ERROR })])
                                alert.show()
                            } else if (validationResult.any { ImportResult res -> res.type == ImportResult.Type.WARNING }) {
                                ULCAlert alert = new I18NAlert(ancestor, "excelImportWarning", [filenames[0], formatValidationResult(validationResult.findAll { ImportResult res -> res.type == ImportResult.Type.WARNING })])
                                alert.addWindowListener([windowClosing: { WindowEvent e -> handleEvent(alert, handler, filenames[0]) }] as IWindowListener)
                                alert.show()
                            } else {
                                doImport(handler, filenames[0])
                            }
                            ancestor?.cursor = Cursor.DEFAULT_CURSOR
                        }] as IFileLoadHandler, selectedFile)

                    }
                },
                onFailure: { reason, description ->
                    if (IFileLoadHandler.CANCELLED != reason) {
                        LOG.error description
                        ULCAlert alert = new I18NAlert(ancestor, "importError")
                        alert.show()
                    }
                    ancestor?.cursor = Cursor.DEFAULT_CURSOR
                }] as IFileChooseHandler, config, ancestor)

    }

    private static String formatValidationResult(List<ImportResult> importResults) {
        StringBuffer sb = new StringBuffer()
        if (importResults.size() > ALERT_ROW_SIZE) {
            ALERT_ROW_SIZE.times { int index ->
                sb << importResults[index].toString() << '\n'
            }
            sb << "(${importResults.size() - ALERT_ROW_SIZE}) more ..."
        } else {
            importResults.each { sb << it.toString() << '\n' }
        }
        return sb.toString()
    }

    private void handleEvent(ULCAlert alert, ExcelImportHandler handler, String filename) {
        if (alert.value.equals(alert.firstButtonLabel)) {
            doImport(handler, filename)
        }
    }

    void doImport(ExcelImportHandler handler, String filename) {
        List<ImportResult> importResult = handler.doImport(filename - ".$extension")
        ULCAlert alert = new I18NAlert(ancestor, "excelImportSuccess", [filename, formatValidationResult(importResult.findAll { ImportResult res -> res.type == ImportResult.Type.SUCCESS })])
        alert.show()
    }


    @Override
    String getExtension() {
        'xlsx'
    }
}
