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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraReportModel extends AbstractReportModel {

    int x = 0
    ResultPathParser parser
    NumberFormat numberFormat = LocaleResources.getNumberFormat()


    public GiraReportModel(Simulation simulation, String modelName) {
        this.simulation = simulation
        this.modelName = modelName
        load()

    }

    Map getParameters() {
        return [:]
    }

    public def getReport() {
        valuesBean = new ResultFunctionValuesBean(simulation?.getSimulationRun(), collector)
        JRBeanCollectionDataSource chartDataSource = getCollectionDataSource()
        return ReportFactory.getGiraReport(chartDataSource, simulation)
    }

    public JRBeanCollectionDataSource getCollectionDataSource() {

        Collection currentValues = new ArrayList()
        getPaths().each {PathType pathType, List<String> componentPaths ->
            Map map = [:]
            JRMapCollectionDataSource waterfallDataSource = getDataSource(JasperChartUtils.generateWaterfallChart(getWaterfallBeans(componentPaths)))
            map["PDFCharts"] = getDataSource(componentPaths, waterfallDataSource)
            currentValues << map
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    JRMapCollectionDataSource getComponentDataSource(Map map) {
        List simpleMasterList = new ArrayList();
        simpleMasterList.add(map);
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    JRMapCollectionDataSource getDataSource(JCommonDrawableRenderer renderer) {
        Map simpleMasterMap = new HashMap();
        simpleMasterMap.put("waterfallChart", renderer);
        List simpleMasterList = new ArrayList();
        simpleMasterList.add(simpleMasterMap);
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    JRMapCollectionDataSource getDataSource(JRBeanCollectionDataSource comments, JRBeanCollectionDataSource values, String pageTitle) {
        Map simpleMasterMap = new HashMap();
        simpleMasterMap.put("comments", comments);
        simpleMasterMap.put("fieldFunctionValues", values);
        simpleMasterMap.put("pageTitle", pageTitle)
        List simpleMasterList = new ArrayList();
        simpleMasterList.add(simpleMasterMap);
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    JRMapCollectionDataSource getDataSource(List<String> componentPaths, JRMapCollectionDataSource waterfallChart) {
        List simpleMasterList = new ArrayList();
        for (String path: componentPaths) {
            getPeriodCount().times {int periodIndex ->
                Map pathPeriodMap = new HashMap();
                String pageTitle = getPageTitle(path, getPathType(path, modelName), periodIndex)
                pathPeriodMap["PDFChartAndCommentsInfo"] = getDataSource(getCommentsDataSource(path, periodIndex), getFieldFunctionValues(path, periodIndex), pageTitle)
                pathPeriodMap["chart"] = getChartDataSource(periodIndex, path)
                pathPeriodMap["waterfallChart"] = waterfallChart
                pathPeriodMap["pageTitle"] = getComponentName(path) + "Overview VaR 99.5%"
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

    public JRBeanCollectionDataSource getCommentsDataSource(String path, int periodIndex) {
        Collection currentValues = new ArrayList<Comment>()
        for (Comment comment: getComments(path, periodIndex)) {
            addCommentData(comment, currentValues)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    public void addCommentData(Comment comment, Collection currentValues) {
        String boxTitle = CommentUtils.getCommentTitle(comment, simulation.modelClass)
        String commentInfo = CommentUtils.getCommentInfo(comment)
        String tags = CommentUtils.getTagsValue(comment).replaceAll("<br>", ", ")
        String addedFiles = UIUtils.getText(CommentReportAction.class, "attachments") + ": " + (comment.getFiles() as List).join(", ")
        currentValues << ["boxTitle": boxTitle, "commentInfo": commentInfo, "tags": tags, "addedFiles": addedFiles, "text": comment.getText()]
    }

    List<Comment> getComments(String path, int periodIndex) {
        List<Comment> comments = []
        fieldNames.each {String fieldName ->
            String commentPath = path + ":" + fieldName
            Collection<Comment> pathFieldComments = simulation.comments.findAll {Comment comment ->
                comment.path == commentPath && (comment.period == -1 || comment.period == periodIndex)
            }
            comments.addAll(pathFieldComments)
        }
        return comments
    }

    protected JRBeanCollectionDataSource getChartDataSource(int periodIndex, String path) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()
        fieldNames.each {String fieldName ->
            Map dbValues = getValues(periodIndex, path, collector, fieldName)
            if (dbValues) {
                try {
                    List xyPairs = JEstimator.gaussKernelBandwidthPdf(dbValues["values"], calcBandwidth(dbValues), false)
                    xyPairs.eachWithIndex {List xyPair, int index ->
                        currentValues << new ReportChartDataBean(x: xyPair[0] * 100 / dbValues["mean"], y: xyPair[1], line: fieldName, period: periodIndex)
                    }
                } catch (Exception ex) {
                }
            }
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected List<ReportWaterfallDataBean> getWaterfallBeans(List<String> paths) {
        if (paths.size() == 0) return []
        List<ReportWaterfallDataBean> beans = []
        Double totalValue = 0
        VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
        String parent = paths[0]
        paths.eachWithIndex {String path, int index ->
            getPeriodCount().times {int periodIndex ->
                try {
                    Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path, collector, "ultimate"))
                    beans << new ReportWaterfallDataBean(line: ResultViewUtils.getResultNodePathShortDisplayName(simulation?.modelClass, path), value: var95 / divider)
                } catch (Exception ex) {}
            }
        }

        beans.sort()
        beans = beans.reverse()

        getPeriodCount().times {int periodIndex ->
            try {
                totalValue += var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(parent, collector, "ultimate"))

            } catch (Exception ex) {}
        }
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: totalValue / divider)
        try {
            double sum = beans.value.sum()
            ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: (totalValue - sum) / divider)

            beans << div
            beans << total
        } catch (Exception ex) {

        }
        return beans
    }


    public JRBeanCollectionDataSource getFieldFunctionValues(String path, int periodIndex) {
        Collection currentValues = new ArrayList()
        fieldNames.each {String fieldName ->
            String meanValue = format(valuesBean.getMean(path, fieldName, periodIndex))
            String var955 = format(valuesBean.getVar(path, fieldName, periodIndex, 99.5))
            String tVar955 = format(valuesBean.getTvar(path, fieldName, periodIndex, 99.5))
            currentValues << ["functionName": UIUtils.getText(GiraReportModel.class, fieldName), "meanValue": meanValue, "varValue": var955, "tVarValue": tVar955]
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    Map<PathType, List<String>> getPaths() {
        List<String> singleValueResultsPaths = ResultAccessor.getPaths(simulation.getSimulationRun())
        parser = new ResultPathParser("GIRA", singleValueResultsPaths)
        Map result = [:]
        ReportUtils.addList(result, PathType.CLAIMSGENERATORS, parser.getComponentPaths(PathType.CLAIMSGENERATORS))
        ReportUtils.addList(result, PathType.LINESOFBUSINESS, parser.getComponentPaths(PathType.LINESOFBUSINESS))
        ReportUtils.addList(result, PathType.REINSURANCE, parser.getComponentPaths(PathType.REINSURANCE))
        return result
    }

    String getPageTitle(String path, String type, int period) {
        String pageTitle = getComponentName(path)
        pageTitle += ResultViewUtils.getResultNodesDisplayName(simulation?.modelClass, path)
        if (type)
            pageTitle += ", " + type
        String periodLabel = getPeriodLabel(period)
        pageTitle += "  Period starting at: " + periodLabel
        return pageTitle
    }

    String getComponentName(String path) {
        String pageTitle = ""
        PathType pathType = parser.getPathType(path)
        if (pathType)
            pageTitle += pathType.getDispalyName() + ": "
        return pageTitle
    }

    public int getPeriodCount() {
        return simulation.periodCount
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


}
