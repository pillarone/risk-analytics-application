package org.pillarone.riskanalytics.application.reports.gira.model

import org.pillarone.riskanalytics.application.reports.ReportModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.util.Estimator
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractReportModel implements ReportModel {

    public static double divider = 1000
    public static List fieldNames = ["ultimate", "paidIncremental", "reserves"]
    protected String modelName = "GIRA"
    public static String collector = AggregatedCollectingModeStrategy.IDENTIFIER
    Map periodLabels = [:]
    private Map cachedValues
    protected Simulation simulation
    protected ResultFunctionValuesBean valuesBean

    public Map getValues(int periodIndex, String path, String collectorName, String fieldName) {
        try {
            SimulationRun run = simulation.getSimulationRun()
            boolean onlyStochasticSeries = ResultAccessor.hasDifferentValues(run, periodIndex, path, collector, fieldName)
            Map map = [:]
            if (onlyStochasticSeries) {
                map["stdDev"] = valuesBean.getStdDev(path, fieldName, periodIndex)
                map["mean"] = valuesBean.getMean(path, fieldName, periodIndex)

                PercentileFunction percentile = new PercentileFunction(75, QuantilePerspective.LOSS)
                Double per75 = percentile.evaluate(run, periodIndex, createRTTN(path, collectorName, fieldName))
                percentile = new PercentileFunction(25, QuantilePerspective.LOSS)
                Double per25 = percentile.evaluate(run, periodIndex, createRTTN(path, collectorName, fieldName))
                if (per75 != null && per25 != null) {
                    map["IQR"] = per75 - per25
                }
                map["values"] = valuesBean.getValues(path, fieldName, periodIndex).sort()
                map["n"] = map["values"].size()
            } else {
                return null
            }
            return map
        } catch (Exception ex) {
            println "getValues $ex"
            ex.printStackTrace()
            return null
        }

    }

    protected double calcBandwidth(Map values) {
        if (values["IQR"] > 0) {
            return Estimator.CONST * Math.min(values["stdDev"], values["IQR"]) * Math.pow(values["n"], (-1 / 5));
        } else {
            return Estimator.CONST * values["stdDev"] * Math.pow(values["n"], (-1 / 5));
        }
    }

    protected ResultTableTreeNode createRTTN(String path, String collectorName, String fieldName) {
        ResultTableTreeNode node = new ResultTableTreeNode("")
        node.resultPath = "${path}:${fieldName}"
        node.collector = collectorName
        return node
    }

    public String getPeriodLabel(int periodIndex) {
        String label
        if (periodLabels[periodIndex]) {
            label = periodLabels[periodIndex]
        } else {
            ResultViewUtils.initPeriodLabels(simulation.getSimulationRun(), periodLabels)
            label = periodLabels[periodIndex]
        }
        return label
    }


}
