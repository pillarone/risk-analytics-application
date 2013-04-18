package org.pillarone.riskanalytics.application.ui.chart.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.util.ExcelExporter
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources

@CompileStatic
class ChartDataExportAction extends ResourceBasedAction {
    ChartView view
    Log LOG = LogFactory.getLog(ChartDataExportAction)

    public ChartDataExportAction(ChartView view) {
        super("ChartDataExportAction")
        this.view = view
    }

    public void doActionPerformed(ActionEvent event) {
        ExcelExporter exporter = new ExcelExporter()
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = getText("dialogTitle")
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "${view.model.title}.xls"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(view.content)
        ClientContext.chooseFile([
                onSuccess: {String[] filePaths, fileNames ->
                    String selectedFile = filePaths[0]
                    selectedFile = selectedFile.endsWith('.xlsx') ?selectedFile:"${selectedFile}.xlsx"

                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            exporter.export view.model.dataTable
                            exporter.addTab UIUtils.getText(this.class, "SimulationSettings"), view.model.simulationSettings
                            exporter.writeWorkBook stream
                        } catch (UnsupportedOperationException t) {
                            LOG.error t.toString()
                            new I18NAlert(ancestor, "exportError").show()
                        } catch (Throwable t) {
                            LOG.error t.toString()
                            new I18NAlert(ancestor, "exportError").show()
                            throw t
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        if (reason != IFileChooseHandler.CANCELLED) {
                            LOG.error description
                            new I18NAlert(ancestor, "exportError").show()
                        }
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)

    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ChartDataExportAction." + key);
    }

}