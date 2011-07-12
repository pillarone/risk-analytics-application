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
import static org.pillarone.riskanalytics.application.reports.gira.model.GiraReportHelper.*
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.application.reports.ReportHelper

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraReportModel extends AbstractReportModel {

    protected ResultPathParser parser
    protected AbstractReportExporter exporter
    protected ChartDataSourceFactory factory


    public GiraReportModel(Simulation simulation, String modelName) {
        this.simulation = simulation
        this.modelName = modelName

    }

    public void init() {
        load()
        List<String> singleValueResultsPaths = ResultAccessor.getPaths(simulation.getSimulationRun())
        parser = new ResultPathParser(modelName, singleValueResultsPaths)
        factory = new ChartDataSourceFactory(modelName: modelName, parser: parser)
        factory.init(simulation)
    }

    Map getParameters() {
        return [:]
    }

    public def getReport() {
        JRBeanCollectionDataSource chartDataSource = getCollectionDataSource()
        Map params = getReportParameters(chartDataSource, simulation)
        exporter.export(params, chartDataSource).toByteArray()
    }

    public JRBeanCollectionDataSource getCollectionDataSource() {

        Collection currentValues = new ArrayList()
        getPaths().each {PathType pathType, List<List<String>> componentPaths ->
            Map map = [:]
            List<ReportWaterfallDataBean> beans = getWaterfallBeans(componentPaths, pathType)
            JRMapCollectionDataSource waterfallDataSource = getRendererDataSource("waterfallChart", JasperChartUtils.generateWaterfallChart(beans))
            map["PDFCharts"] = factory.getPDFChartsDataSource(componentPaths, waterfallDataSource, pathType)
            currentValues << map
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    List<ReportWaterfallDataBean> getWaterfallBeans(List<List<String>> componentPaths, PathType pathType) {
        List<String> netPaths = parser.getPathsByPathType(componentPaths, pathType)
        AbstractWaterfallChart instance = AbstractWaterfallChart.getInstance(pathType, simulation, parser)
        List<ReportWaterfallDataBean> beans = instance.getBeans(netPaths)
        return beans
    }

    Map<PathType, List<List<String>>> getPaths() {
        Map result = [:]
        IPathFilter claimsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.CLAIMSGENERATORS), ResultPathParser.CLAIMS_SUFFIX_LIST)
        IPathFilter reinsuranceFilter = PathFilter.getFilter(parser.getComponentPath(PathType.REINSURANCE), ResultPathParser.REINSURANCE_TABLE_SUFFIX_LIST)
        ReportUtils.addList(result, PathType.CLAIMSGENERATORS, parser.getComponentPaths(PathType.CLAIMSGENERATORS, claimsFilter))
        ReportUtils.addList(result, PathType.REINSURANCE, parser.getComponentPaths(PathType.REINSURANCE, reinsuranceFilter))
        return result
    }


    void load() {
        if (simulation && !simulation.isLoaded())
            this.simulation.load()
    }


    public void setExporter(AbstractReportExporter exporter) {
        this.exporter = exporter
    }

    private Map getReportParameters(JRBeanCollectionDataSource chartsDataSource, ModellingItem modellingItem) {
        Map params = new HashMap()
        params["charts"] = chartsDataSource
        params["title"] = "Example Report"
        params["footer"] = GiraReportHelper.getFooter()
        params["infos"] = ReportUtils.getItemInfo(modellingItem)
        Person currentUser = UserManagement.getCurrentUser()
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "GiraReport"
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pillarone-logo-transparent-background-report.png")
        return params
    }


}
