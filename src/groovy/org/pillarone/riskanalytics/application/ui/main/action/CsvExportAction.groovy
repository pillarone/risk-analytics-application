package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import java.util.regex.Pattern
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.ExcelExporter
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.util.IConfigObjectWriter
import org.springframework.transaction.TransactionStatus
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CsvExportAction extends ExportItemAction {

    public CsvExportAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(tree, model, "CsvExportAction")
    }

    protected void exportItem(Simulation item, int itemCount, filePaths, ULCWindow ancestor) {
        SingleValueResult.withTransaction {trx ->
            def simulationFileName = "${item.name}.csv".replaceAll(':', '-')
            String selectedFile = itemCount > 1 ? "${filePaths[0]}/$simulationFileName" : filePaths[0]
            ClientContext.storeFile([prepareFile: {OutputStream stream ->
                try {
                    item.load()
                    def simulationRun = item.simulationRun
                    String fileName = ResultAccessor.exportCsv(simulationRun)
                    if (fileName) {
                        FileInputStream fis = new FileInputStream(fileName)
                        stream.write("ITERATION,PERIOD,PATH,VALUE,COLLECTOR\n".bytes)
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
            }] as IFileStoreHandler, selectedFile)
        }

    }

    protected boolean validate(List items) {
        return true
    }
}
