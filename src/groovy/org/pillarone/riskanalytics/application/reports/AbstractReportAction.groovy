package org.pillarone.riskanalytics.application.reports

import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.UserPreferences
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractReportAction extends SelectionTreeAction {
    protected UserPreferences userPreferences = new UserPreferences()

    Log LOG = LogFactory.getLog(AbstractReportAction)

    def AbstractReportAction(name, tree, RiskAnalyticsMainModel model) {
        super(name, tree, model)
    }

    public AbstractReportAction(String title) {
        super(title);
    }

    public void saveReport(def output, String fileName, ULCComponent component) {
        FileChooserConfig config = new FileChooserConfig()
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.REPORT_DIR_KEY))
        config.dialogTitle = "Save Report As"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        fileName = fileName.replace(":", "")
        fileName = fileName.replace("/", "")
        fileName = fileName.replace("*", "")
        fileName = fileName.replace("?", "")
        fileName = fileName.replace("\"", "")
        fileName = fileName.replace("<", "")
        fileName = fileName.replace(">", "")
        fileName = fileName.replace("|", "")
        config.selectedFile = fileName

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(component)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]
                    userPreferences.setUserDirectory(UserPreferences.REPORT_DIR_KEY, filePaths[0])
                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            stream.write output
                        } catch (UnsupportedOperationException t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                            LOG.error "Saving Report Failed: ${t}"
                        } catch (Throwable t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                            LOG.error "Saving Report Failed: ${t}"
                            throw t
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                        LOG.error "Saving Report Failed: Description: ${description} Reason: ${reason}"
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                    new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                    LOG.error "Saving Report Failed: Description: ${description} Reason: ${reason}"
                }] as IFileChooseHandler, config, ancestor)
    }

}
