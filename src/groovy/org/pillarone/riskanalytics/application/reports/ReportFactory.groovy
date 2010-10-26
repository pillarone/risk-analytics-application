package org.pillarone.riskanalytics.application.reports

import jasper.JasperService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.reports.bean.AllRVTableDataBean
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodActuaryReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodManagementReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle4PeriodsReportModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Simulation

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

    static protected JasperService getJasperService(Simulation simulation) {
        return (JasperService) ApplicationHolder.application.mainContext.getBean("jasperService")
    }
}