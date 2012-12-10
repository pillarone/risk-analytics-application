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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportRawDataTable extends ResourceBasedAction {
    ResultIterationDataView view


    public ExportRawDataTable(ResultIterationDataView view) {
        super("ExportRawDataTable")
        this.view = view
    }

    public void doActionPerformed(ActionEvent event) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Result Iteration Data Export"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "ResultIterationData.xlsx"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(view.content)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]

                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            ExcelExporter exporter = new ExcelExporter()
                            exporter.headers = view.model.columnHeader
                            exporter.exportResults(view.model.rawData)
                            exporter.addTab "Simulation Settings", view.model.simulationSettings
                            exporter.addTab "Runtime parameters", view.model.runtimeParameters
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
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)
    }


}

