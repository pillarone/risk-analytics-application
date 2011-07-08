package org.pillarone.riskanalytics.application.reports.gira.model

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.reports.bean.ExpectedLossChartDataBean
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction
import org.pillarone.riskanalytics.application.reports.bean.ReportChartDataBean
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.reports.bean.LoBMeanStdDevIQRPerDataBean
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.reports.bean.LoBReportBruttoTableDataBean
import org.pillarone.riskanalytics.application.reports.ReportFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import org.pillarone.riskanalytics.application.reports.comment.action.CommentReportAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.reports.gira.action.ResultPathParser
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.application.reports.gira.action.PathType
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils
import org.pillarone.riskanalytics.core.parameter.Parameter
import org.pillarone.riskanalytics.core.parameter.EnumParameter
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.reports.bean.ReportWaterfallDataBean
import org.pillarone.riskanalytics.application.reports.JasperChartUtils
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import net.sf.jasperreports.renderers.JCommonDrawableRenderer
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.application.util.ReportUtils
import static org.pillarone.riskanalytics.application.util.ReportUtils.*
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.user.Person

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraReportModel extends AbstractReportModel {

    ResultPathParser parser
    NumberFormat numberFormat = LocaleResources.getNumberFormat()
    GiraReportHelper reportHelper
    private AbstractReportExporter exporter


    public GiraReportModel(Simulation simulation, String modelName) {
        this.simulation = simulation
        this.modelName = modelName
        load()
        reportHelper = new GiraReportHelper(simulation: simulation)
    }

    Map getParameters() {
        return [:]
    }

    public def getReport() {
        valuesBean = new ResultFunctionValuesBean(simulation?.getSimulationRun(), collector)
        JRBeanCollectionDataSource chartDataSource = getCollectionDataSource()
        Map params = getReportParameters(chartDataSource, simulation)
        exporter.export(params, chartDataSource).toByteArray()
    }

    public JRBeanCollectionDataSource getCollectionDataSource() {

        Collection currentValues = new ArrayList()
        getPaths().each {PathType pathType, List<List<String>> componentPaths ->
            Map map = [:]
            List<String> netPaths = parser.getPathsByPathType(componentPaths, pathType)
            List<ReportWaterfallDataBean> beans = getWaterfallBeans(netPaths)
            JRMapCollectionDataSource waterfallDataSource = getRendererDataSource("waterfallChart", JasperChartUtils.generateWaterfallChart(beans))
            map["PDFCharts"] = getPDFChartsDataSource(componentPaths, waterfallDataSource)
            currentValues << map
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    JRMapCollectionDataSource getPDFChartsDataSource(List<List<String>> componentPaths, JRMapCollectionDataSource waterfallChart) {
        List simpleMasterList = new ArrayList();
        for (List<String> paths: componentPaths) {
            getPeriodCount().times {int periodIndex ->
                Map pathPeriodMap = new HashMap();
                String pageTitle = reportHelper.getPageTitle(parser, paths[0], getPathType(paths[0], modelName), periodIndex)
                JRBeanCollectionDataSource fieldFunctionValues = getFieldFunctionValues(paths, periodIndex)
                pathPeriodMap["PDFChartAndCommentsInfo"] = getPDFChartAndCommentsInfoDataSource(reportHelper.getCommentsDataSource(paths[0], periodIndex), fieldFunctionValues, pageTitle)
                pathPeriodMap["chart"] = getChartDataSource(periodIndex, paths)
                pathPeriodMap["waterfallChart"] = waterfallChart
                pathPeriodMap["pageTitle"] = reportHelper.getComponentName(parser, paths[0]) + "Overview VaR 99.5% (ultimate)"
                simpleMasterList << pathPeriodMap
            }
        }
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    String getPathType(String path, String modelName) {
        String typePath = path.substring((modelName + ":").length()) + ":type"
        Parameter parameter = Parameter.findByPath(typePath)
        return ((parameter instanceof EnumParameter) ? parameter.parameterType : "")
    }

    protected JRMapCollectionDataSource getChartDataSource(int periodIndex, List componentPaths) {
        Map seriesMap = [:]
        parser.appyFilter(componentPaths).each {String path ->
            String suffix = path.substring(path.lastIndexOf(":") + 1)
            fieldNames.each {String fieldName ->
                Map dbValues = getValues(periodIndex, path, collector, fieldName)
                if (dbValues) {
                    try {
                        seriesMap[UIUtils.getText(GiraReportModel.class, fieldName + suffix)] = JEstimator.gaussKernelBandwidthPdf(dbValues["values"], calcBandwidth(dbValues), false)
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return getRendererDataSource("pdfChart", JasperChartUtils.generatePDFChart(seriesMap))
    }

    protected List<ReportWaterfallDataBean> getWaterfallBeans(List<String> paths) {
        if (paths.size() == 0) return []
        List<ReportWaterfallDataBean> beans = []
        Double totalValue = 0
        VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
        String parent = paths[0]
        int periodIndex = 0
        double sum = 0
        paths.eachWithIndex {String path, int index ->
            if (!parser.isParentPath(path)) {
                try {
                    Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path, collector, "ultimate"))
                    sum += var95
                    beans << new ReportWaterfallDataBean(line: ResultViewUtils.getResultNodePathShortDisplayName(simulation?.modelClass, path), value: var95)
                } catch (Exception ex) {}
            }

        }


        try {
            totalValue = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(parent, collector, "ultimate"))
        } catch (Exception ex) {}
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: totalValue)
        try {
            ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: (totalValue - sum))

            beans << div
            beans << total
        } catch (Exception ex) {

        }
        return beans
    }


    public JRBeanCollectionDataSource getFieldFunctionValues(List<String> paths, int periodIndex) {
        Collection currentValues = new ArrayList()
        paths.each {String path ->
            String suffix = path.substring(path.lastIndexOf(":") + 1)
            fieldNames.each {String fieldName ->
                String meanValue = format(valuesBean.getMean(path, fieldName, periodIndex))
                String var955 = format(valuesBean.getVar(path, fieldName, periodIndex, 99.5))
                String tVar955 = format(valuesBean.getTvar(path, fieldName, periodIndex, 99.5))
                currentValues << ["functionName": UIUtils.getText(GiraReportModel.class, fieldName + suffix), "meanValue": meanValue, "varValue": var955, "tVarValue": tVar955]
            }
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    Map<PathType, List<List<String>>> getPaths() {
        List<String> singleValueResultsPaths = ResultAccessor.getPaths(simulation.getSimulationRun())
        parser = new ResultPathParser(modelName, singleValueResultsPaths)

        Map result = [:]
        IPathFilter claimsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.CLAIMSGENERATORS), ResultPathParser.CLAIMS_SUFFIX_LIST)
        IPathFilter reinsuranceFilter = PathFilter.getFilter(parser.getComponentPath(PathType.REINSURANCE), ResultPathParser.REINSURANCE_TABLE_SUFFIX_LIST)
        ReportUtils.addList(result, PathType.CLAIMSGENERATORS, parser.getComponentPaths(PathType.CLAIMSGENERATORS, claimsFilter))
        ReportUtils.addList(result, PathType.REINSURANCE, parser.getComponentPaths(PathType.REINSURANCE, reinsuranceFilter))
        return result
    }

    public int getPeriodCount() {
        return reportHelper.periodCount
    }

    void load() {
        if (simulation && !simulation.isLoaded())
            this.simulation.load()
    }

    String format(Double value) {
        try {
            return numberFormat.format(value)
        } catch (Exception ex) {
            return "-"
        }
    }

    public void setExporter(AbstractReportExporter exporter) {
        this.exporter = exporter
    }

    private Map getReportParameters(JRBeanCollectionDataSource chartsDataSource, ModellingItem modellingItem) {
        Map params = new HashMap()
        params["charts"] = chartsDataSource
        params["title"] = "Example Report"
        Person currentUser = UserManagement.getCurrentUser()
        params["footer"] = currentUser ? UIUtils.getText(ReportFactory.class, "footerByUser", [currentUser.username]) : UIUtils.getText(ReportFactory.class, "footer")
        params["infos"] = ReportUtils.getItemInfo(modellingItem)
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "GiraReport"
        params["SUBREPORT_DIR"] = reportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pillarone-logo-transparent-background-report.png")
        return params
    }


}
