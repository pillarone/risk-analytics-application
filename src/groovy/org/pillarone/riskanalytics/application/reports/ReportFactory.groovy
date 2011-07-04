package org.pillarone.riskanalytics.application.reports

import jasper.JasperService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.reports.bean.AllRVTableDataBean
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodActuaryReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle1PeriodManagementReportModel
import org.pillarone.riskanalytics.application.reports.model.CapitalEagle4PeriodsReportModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource

import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel

import org.pillarone.riskanalytics.application.reports.bean.PropertyValuePairBean
import org.pillarone.riskanalytics.application.reports.gira.model.GiraReportModel

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

    static def getGiraReport(JRBeanCollectionDataSource chartsDataSource, ModellingItem modellingItem) {
        Map params = new HashMap()
        params["charts"] = chartsDataSource
        params["title"] = "Example Report"
        Person currentUser = UserManagement.getCurrentUser()
        params["footer"] = currentUser ? UIUtils.getText(ReportFactory.class, "footerByUser", [currentUser.username]) : UIUtils.getText(ReportFactory.class, "footer")
        params["infos"] = getItemInfo(modellingItem)
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "GiraReport"
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pillarone-logo-transparent background report.jpg")
        return ReportHelper.getReportOutputStream(params, chartsDataSource).toByteArray()
    }

    static def getReport(JRBeanCollectionDataSource collectionDataSource, ModellingItem modellingItem) {

        Map params = new HashMap()
        params["comments"] = collectionDataSource
        params["title"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName)
        Person currentUser = UserContext.getCurrentUser()
        params["footer"] = currentUser ? UIUtils.getText(ReportFactory.class, "footerByUser", [currentUser.username]) : UIUtils.getText(ReportFactory.class, "footer")
        params["infos"] = getItemInfo(modellingItem)
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "CommentReport"
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pdf-reports-header.png")
        return ReportHelper.getReportOutputStream(params, collectionDataSource).toByteArray()
    }

    static protected JasperService getJasperService(Simulation simulation) {
        return (JasperService) ApplicationHolder.application.mainContext.getBean("jasperService")
    }

    static JRBeanCollectionDataSource getItemInfo(Parameterization parameterization) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Name"), value: parameterization.name + " v" + parameterization.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Model"), value: parameterization.modelClass.simpleName)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "State"), value: parameterization.status)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Tags"), value: parameterization?.tags?.join(","))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Owner"), value: (parameterization.creator ? parameterization.creator.username : ""))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Created"), value: DateFormatUtils.formatDetailed(parameterization.creationDate))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "LastUpdateBy"), value: (parameterization.lastUpdater ? parameterization.lastUpdater.username : ""))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "LastModification"), value: parameterization.modificationDate ? DateFormatUtils.formatDetailed(parameterization.modificationDate) : "")
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    static JRBeanCollectionDataSource getItemInfo(Simulation simulation) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()

        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Name"), value: simulation.name)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Model"), value: simulation.modelClass.simpleName)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Parameter"), value: simulation.parameterization.name + " v" + simulation.parameterization.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "ResultTemplate"), value: simulation.template.name + " v" + simulation.template.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "NumberOfPeriods"), value: simulation.periodCount)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "NumberOfIterations"), value: simulation.numberOfIterations)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Owner"), value: simulation.creator ? simulation.creator.username : "")
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "StartTime"), value: DateFormatUtils.formatDetailed(simulation.start))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "EndTime"), value: DateFormatUtils.formatDetailed(simulation.end))
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }


}