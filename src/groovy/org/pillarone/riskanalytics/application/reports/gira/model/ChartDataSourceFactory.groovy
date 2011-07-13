package org.pillarone.riskanalytics.application.reports.gira.model

import org.pillarone.riskanalytics.application.reports.gira.action.ResultPathParser
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import static org.pillarone.riskanalytics.application.reports.gira.model.GiraReportHelper.*
import static org.pillarone.riskanalytics.application.util.ReportUtils.*
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.application.reports.JasperChartUtils
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.reports.bean.ReportWaterfallDataBean
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction
import org.pillarone.riskanalytics.application.reports.gira.action.PathType
import org.pillarone.riskanalytics.application.util.ReportUtils
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.util.SeriesColor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ChartDataSourceFactory {

    protected ResultPathParser parser
    protected GiraReportHelper reportHelper
    protected ResultFunctionValuesBean valuesBean
    String modelName
    private colorsMap = [:]

    public void init(Simulation simulation) {
        reportHelper = new GiraReportHelper(simulation: simulation)
        valuesBean = new ResultFunctionValuesBean(reportHelper.getSimulationRun(), AbstractReportModel.collector)
    }

    JRMapCollectionDataSource getPDFChartsDataSource(List<List<String>> componentPaths, JRMapCollectionDataSource waterfallChart, PathType pathType) {
        List simpleMasterList = new ArrayList();
        for (List<String> paths: componentPaths) {
            getPeriodCount().times {int periodIndex ->
                Map pathPeriodMap = new HashMap();
                String pageTitle = reportHelper.getPageTitle(parser, paths[0], getPathType(paths[0], modelName), periodIndex)
                JRBeanCollectionDataSource fieldFunctionValues = getFieldFunctionValues(paths, periodIndex)
                pathPeriodMap["PDFChartAndCommentsInfo"] = getPDFChartAndCommentsInfoDataSource(reportHelper.getCommentsDataSource(paths, periodIndex), fieldFunctionValues, pageTitle)
                pathPeriodMap["chart"] = getChartDataSource(periodIndex, paths)
                pathPeriodMap["waterfallChart"] = waterfallChart
                pathPeriodMap["pageTitle"] = reportHelper.getComponentName(parser, paths[0]) + " " + reportHelper.getPageTitle(parser, paths[0], "", periodIndex)
                pathPeriodMap["overViewPageTitle"] = reportHelper.getComponentName(parser, paths[0]) + " " + AbstractWaterfallChart.getTitle(pathType)
                simpleMasterList << pathPeriodMap
            }
        }
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    protected JRMapCollectionDataSource getChartDataSource(int periodIndex, List componentPaths) {
        Map seriesMap = [:]
        parser.appyFilter(componentPaths).each {String path ->
            String suffix = path.substring(path.lastIndexOf(":") + 1)
            AbstractReportModel.fieldNames.each {String fieldName ->
                Map dbValues = getValues(periodIndex, path, AbstractReportModel.collector, fieldName)
                if (dbValues) {
                    try {
                        String text = UIUtils.getText(GiraReportModel.class, fieldName + suffix)
                        seriesMap[text] = JEstimator.gaussKernelBandwidthPdf(dbValues["values"], calcBandwidth(dbValues), false)
                        putColor(text)
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return getRendererDataSource("pdfChart", JasperChartUtils.generatePDFChart(seriesMap, colorsMap))
    }

    public JRBeanCollectionDataSource getFieldFunctionValues(List<String> paths, int periodIndex) {
        Collection currentValues = new ArrayList()
        paths.each {String path ->
            String suffix = path.substring(path.lastIndexOf(":") + 1)
            AbstractReportModel.fieldNames.each {String fieldName ->
                String meanValue = format(valuesBean.getMean(path, fieldName, periodIndex))
                String var955 = format(valuesBean.getVar(path, fieldName, periodIndex, 99.5))
                String tVar955 = format(valuesBean.getTvar(path, fieldName, periodIndex, 99.5))
                currentValues << ["functionName": UIUtils.getText(GiraReportModel.class, fieldName + suffix), "meanValue": meanValue, "varValue": var955, "tVarValue": tVar955]
            }
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected double calcBandwidth(Map values) {
        return JEstimator.calcBandwidthForGaussKernelEstimate((double) values["stdDev"], (double) values["IQR"], values["n"])
    }

    int getPeriodCount() {
        reportHelper.periodCount
    }

    private String getPathType(String path, String modelName) {
        //todo
        return ""
    }

    private Map getValues(int periodIndex, String path, String collectorName, String fieldName) {
        try {
            SimulationRun run = reportHelper.getSimulationRun()
            boolean onlyStochasticSeries = ResultAccessor.hasDifferentValues(run, periodIndex, path, AbstractReportModel.collector, fieldName)
            Map map = [:]
            if (onlyStochasticSeries) {
                map["stdDev"] = valuesBean.getStdDev(path, fieldName, periodIndex)
                map["mean"] = valuesBean.getMean(path, fieldName, periodIndex)

                PercentileFunction percentile = new PercentileFunction(75, QuantilePerspective.LOSS)
                Double per75 = percentile.evaluate(run, periodIndex, ResultViewUtils.createRTTN(path, collectorName, fieldName))
                percentile = new PercentileFunction(25, QuantilePerspective.LOSS)
                Double per25 = percentile.evaluate(run, periodIndex, ResultViewUtils.createRTTN(path, collectorName, fieldName))
                if (per75 != null && per25 != null) {
                    map["IQR"] = per75 - per25
                }
                map["values"] = valuesBean.getValues(path, fieldName, periodIndex).sort()
                map["n"] = map["values"].size()
            } else {
                map["stdDev"] = 0
                map["mean"] = 0
                map["IQR"] = 0
                map["values"] = []
                map["n"] = 0
            }
            return map
        } catch (Exception ex) {
            ex.printStackTrace()
            return null
        }

    }

    void putColor(String key) {
        if (!colorsMap.containsKey(key)) {
            colorsMap[key] = SeriesColor.seriesColorList[colorsMap.keySet().size() + 1]
        }
    }


}