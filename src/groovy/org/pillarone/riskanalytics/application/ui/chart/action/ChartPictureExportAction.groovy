package org.pillarone.riskanalytics.application.ui.chart.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.serializable.IFileChooseHandler
import com.ulcjava.base.application.util.serializable.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources

class ChartPictureExportAction extends ResourceBasedAction {
    ChartView view
    Log LOG = LogFactory.getLog(ChartPictureExportAction)

    public ChartPictureExportAction(ChartView view) {
        super("ChartPictureExportAction")
        this.view = view
    }

    public void doActionPerformed(ActionEvent event) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = getText("dialogTitle")
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "chart.png"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(view.content)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]

                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            view.writeChartInStream stream
                        } catch (Throwable t) {
                            LOG.error t.toString()
                            new I18NAlert(ancestor, "exportError").show()
                            throw t
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        LOG.error description
                        new I18NAlert(ancestor, "exportError").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                    if (reason != IFileChooseHandler.CANCELLED) {
                        LOG.error description
                        new I18NAlert(ancestor, "exportError").show()
                    }
                }] as IFileChooseHandler, config, ancestor)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ChartPictureExportAction." + key);
    }

}