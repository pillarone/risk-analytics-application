package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.util.IFileStoreHandler
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CsvExportAction extends ExportItemAction {

    public CsvExportAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(tree, model, "CsvExportAction",'csv')
    }

    protected void exportItem(Simulation item, int itemCount, filePaths, ULCWindow ancestor) {
        SingleValueResult.withTransaction {trx ->
            String selectedFile = itemCount > 1 ? "${filePaths[0]}/${getFileName(item)}" : filePaths[0]
            ClientContext.storeFile([prepareFile: {OutputStream stream ->
                try {
                    item.load()
                    def simulationRun = item.simulationRun
                    String fileName = ResultAccessor.exportCsv(simulationRun)
                    if (fileName) {
                        FileInputStream fis = new FileInputStream(fileName)
                        stream.write("ITERATION,PERIOD,PATH,FIELD,VALUE,COLLECTOR,DATE\n".bytes)
                        stream.write fis.getBytes()
                        stream.write(UIUtils.toCSV(getSimulationSettings(simulationRun)).getBytes())
                    } else
                        showAlert("exportError")
                } catch (Throwable t) {
                    LOG.error("Export failed: " + t.message, t)
                    showAlert("exportError")
                } finally {
                    stream.close()
                }
            }, onSuccess: {path, name ->
            }, onFailure: {reason, description ->
                LOG.error description
                showAlert("exportError")
            }] as IFileStoreHandler, selectedFile, Long.MAX_VALUE, true, false)
        }

    }

    protected boolean validate(List items) {
        return true
    }
}
