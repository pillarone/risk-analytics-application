package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.util.ExcelExporter
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import com.ulcjava.base.application.ULCComponent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportRawDataTable extends ResourceBasedAction {

    private static Log LOG = LogFactory.getLog(ExportRawDataTable)

    ResultIterationDataViewModel model
    ULCComponent dialogRoot


    public ExportRawDataTable(ResultIterationDataViewModel model, ULCComponent dialogRoot) {
        super("ExportRawDataTable")
        this.model = model
        this.dialogRoot = dialogRoot
    }

    public void doActionPerformed(ActionEvent event) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Result Iteration Data Export"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "ResultIterationData.xlsx"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(dialogRoot)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]
                    selectedFile = selectedFile.endsWith('.xlsx') ?selectedFile:"${selectedFile}.xlsx"

                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            ExcelExporter exporter = new ExcelExporter()
                            exporter.headers = model.columnHeader
                            exporter.exportResults(model.rawData)
                            exporter.addTab "Simulation Settings", model.simulationSettings
                            exporter.addTab "Runtime parameters", model.runtimeParameters
                            exporter.writeWorkBook stream
                        } catch (UnsupportedOperationException t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                        } catch (Throwable t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                            throw t
                        } catch (Exception ex) {
                            new ULCAlert(ancestor, "Export failed", ex.message, "Ok").show()
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {String path, String name ->
                    }, onFailure: { int reason, String description ->
                        LOG.error("Excel export failed: ${description}")
                        new ULCAlert(ancestor, "Export failed", "Failed to write to file, maybe it is already open?", "Ok").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)
    }


}

