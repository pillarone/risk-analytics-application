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
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.comment.action.FileStoreHandler
import com.ulcjava.base.application.util.DefaultFileStoreHandler
import com.canoo.common.FileUtilities

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
        try {
            String targetFile = getTargetDir() + File.separator + fileName
            FileUtilities.addFileToDirectory(getTargetDir(), fileName, output)
            LOG.info "$targetFile saved successfully"
        } catch (Exception ex) {
            LOG.error "$ex"
        }
    }

    public void open(String fileName) {
        String targetFile = getTargetDir() + File.separator + fileName
        File file = new File(targetFile)
        if (file.exists()) {
            ClientContext.showDocument(targetFile, "_new")
        } else {
            LOG.error "file $targetFile doesn't exist"
        }

    }

    abstract String getTargetDir()

    String validateFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9]", "")
    }

}
