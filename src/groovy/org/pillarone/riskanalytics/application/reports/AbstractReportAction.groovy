package org.pillarone.riskanalytics.application.reports

import com.ulcjava.base.application.ULCComponent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory

import org.pillarone.riskanalytics.application.document.ShowDocumentStrategyFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractReportAction extends SelectionTreeAction {

    protected UserPreferences userPreferences = UserPreferencesFactory.getUserPreferences()

    Log LOG = LogFactory.getLog(AbstractReportAction)

    def AbstractReportAction(name, tree, RiskAnalyticsMainModel model) {
        super(name, tree, model)
    }

    public AbstractReportAction(String title) {
        super(title);
    }

    public void saveReport(byte[] output, String fileName, ULCComponent component) {
        ShowDocumentStrategyFactory.getInstance().showDocument(fileName, output, "application/pdf")
    }

    abstract String getTargetDir()

    String validateFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9]", "")
    }

}
