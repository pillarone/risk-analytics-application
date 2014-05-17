package org.pillarone.riskanalytics.application.reports
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.document.ShowDocumentStrategyFactory
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractReportAction extends SelectionTreeAction {

    protected UserPreferences userPreferences = UserPreferencesFactory.getUserPreferences()

    AbstractReportAction(String name, ULCTableTree tree) {
        super(name, tree)
    }

    AbstractReportAction(String title) {
        super(title);
    }

    void saveReport(def output, String fileName, ULCComponent component) {
        ShowDocumentStrategyFactory.getInstance().showDocument(fileName, output, "application/pdf")
    }

    abstract String getTargetDir()

    String validateFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9]", "")
    }
}
