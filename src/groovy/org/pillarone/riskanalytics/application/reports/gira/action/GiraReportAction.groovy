package org.pillarone.riskanalytics.application.reports.gira.action

import org.pillarone.riskanalytics.application.reports.AbstractReportAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.reports.ReportFactory
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import org.pillarone.riskanalytics.application.reports.gira.model.GiraReportModel
import org.pillarone.riskanalytics.application.reports.ReportModel
import org.pillarone.riskanalytics.application.reports.comment.action.CommentReportAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.reports.gira.model.PDFReportExporter
import org.pillarone.riskanalytics.core.FileConstants
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import com.ulcjava.base.application.UlcUtilities

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraReportAction extends AbstractReportAction {
    GiraReportModel model
    RiskAnalyticsMainModel mainModel
    ULCTableTree tree

    public GiraReportAction() {
        super("GiraReportAction")
    }

    public GiraReportAction(ULCTableTree tree, RiskAnalyticsMainModel mainModel) {
        super("GiraReportAction", tree, mainModel)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        try {
            Simulation simulation = getSelectedItem()
            initReportModel(simulation)
            model.setExporter(new PDFReportExporter())
            String fileName = getFileName(simulation)
            saveReport(model.getReport(), fileName, event?.source)
            open(fileName)
        } catch (IllegalArgumentException e) {
            new I18NAlert(UlcUtilities.getWindowAncestor(tree), "PDFReport").show()
            LOG.error "Can not create report: ${e.message} Stacktrace: ${e.stackTrace}"
        }
    }

    void initReportModel(Simulation simulation) {
        Model selectedModel = getSelectedModel()
        model = new GiraReportModel(simulation, selectedModel.name)
        model.init()
    }

    private void printPaths(Simulation simulation) {
        if (!LocaleResources.getTestMode() && !simulation.isLoaded()) simulation.load()
        List paths = ResultAccessor.getPaths(simulation.getSimulationRun())
        paths.each {
            println it
        }
    }

    String getFileName(Simulation simulation) {
        return validateFileName(simulation.name) + ".pdf"
    }

    @Override
    String getTargetDir() {
        return FileConstants.REPORT_PDF_DIRECTORY
    }


}