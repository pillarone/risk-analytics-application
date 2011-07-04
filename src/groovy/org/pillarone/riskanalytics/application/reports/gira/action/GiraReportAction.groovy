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
            saveReport(model.getReport(), getFileName(simulation), event?.source)
        } catch (IllegalArgumentException e) {
            LOG.error "Can not create report: ${e.message} Stacktrace: ${e.stackTrace}"
        }
    }

    void initReportModel(Simulation simulation) {
        model = new GiraReportModel(simulation)
    }

    private void printPaths(Simulation simulation) {
        if (!LocaleResources.getTestMode() && !simulation.isLoaded()) simulation.load()
        List paths = ResultAccessor.getPaths(simulation.getSimulationRun())
        paths.each {
            println it
        }
    }

    String getFileName(Simulation simulation) {
        return simulation.name + ".pdf"
    }

}