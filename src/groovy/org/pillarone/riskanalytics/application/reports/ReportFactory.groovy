package org.pillarone.riskanalytics.application.reports

import jasper.JasperService
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.reports.bean.AllRVTableDataBean
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodActuaryReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodManagementReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle4PeriodsReportModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.ReportUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.user.Person

public class ReportFactory {
    public static String REPORT_DIR = '/reports'
    public static String SERVER_REPORT_DIR = '/reports'

    public static boolean testMode = false
    public static boolean generationSuccessful = false

    static ReportModel getReportModel(Simulation simulation, String reportName) {
        switch (simulation.modelClass.simpleName) {
            case "CapitalEagleModel":
                switch (simulation.periodCount) {
                    case 1:
                        if (reportName == "Management Summary") {
                            return new CapitalEagle1PeriodManagementReportModel(simulation)
                        } else {
                            return new CapitalEagle1PeriodActuaryReportModel(simulation)
                        }
                    case 4:
                        return new CapitalEagle4PeriodsReportModel(simulation)
                }

        }
        throw new IllegalArgumentException("Report generation not supported for this model / # of period")
    }

    static def getReport(Simulation simulation, String reportName) {
        ReportModel model = ReportFactory.getReportModel(simulation, reportName)

        Map params = model.parameters
        params["_file"] = model.reportFileName
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["SimulationSettings"] = JasperChartUtils.createSimulationSettingsDataSource(simulation, false)
        params["SimulationSettingsSmall"] = JasperChartUtils.createSimulationSettingsDataSource(simulation, true)
        params["p1Icon"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "application.png")
        if (!params["p1Logo"]) {
            params["p1Logo"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "pdf-reports-header.png")
        }
        params["Comment"] = simulation.comment ? simulation.comment : ""
        Collection collection = model.prepareData()
        if (collection == null) {
            collection = [new AllRVTableDataBean(type: "type")]
        }
        def output = ReportHelper.generateReport(collection, params).toByteArray()
        if (testMode) {
            generationSuccessful = true
        }
        return output
    }

    static def getReport(JRBeanCollectionDataSource collectionDataSource, ModellingItem modellingItem) {

        Map params = new HashMap()
        params["comments"] = collectionDataSource
        params["title"] = UIUtils.getText(ReportFactory.class, "title", [ReportUtils.getItemName(modellingItem)] as List)
        Person currentUser = UserContext.getCurrentUser()
        String footerValue = currentUser ? UIUtils.getText(ReportFactory.class, "footerByUser", [currentUser.username]) : UIUtils.getText(ReportFactory.class, "footer")
        footerValue += " " + DateFormatUtils.formatDetailed(new DateTime())
        params["footer"] = footerValue
        params["infos"] = ReportUtils.getItemInfo(modellingItem)
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "CommentReport"
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pdf-reports-header.png")
        return ReportHelper.getReportOutputStream(params, collectionDataSource).toByteArray()
    }

}