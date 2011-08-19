package org.pillarone.riskanalytics.application.reports.model

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction
import org.pillarone.riskanalytics.application.reports.CapitalEagleReportModel
import org.pillarone.riskanalytics.application.reports.JasperChartUtils
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.reports.bean.*

class CapitalEagle1PeriodManagementReportModel extends CapitalEagleReportModel {

    protected boolean validateSimulation() {
        if (simulation.modelClass.simpleName != "CapitalEagleModel") {
            throw new IllegalArgumentException("wrong model")
        }
        if (simulation.periodCount != 1) {
            throw new IllegalArgumentException("not 1 periods")
        }
        List paths = ResultAccessor.getPaths(simulation.getSimulationRun())

        validatePathMap(paths, grossPathMap)
        validatePathMap(paths, netPathMap)
        validatePathMap(paths, singleClaimsPathMap)
        validatePathMap(paths, attritionalClaimsPathMap)
        validatePathMap(paths, grossPremiumPathMap)
        validatePathMap(paths, netPremiumPathMap)
        validatePathMap(paths, summaryPathMap)
        validatePathMap(paths, morePropertyClaimsPathMap)
    }


    public CapitalEagle1PeriodManagementReportModel(Simulation simulation) {
        this.simulation = simulation
        if (!simulation.isLoaded()) simulation.load()
        validateSimulation()
    }

    public CapitalEagle1PeriodManagementReportModel(Object object) {
        throw new IllegalAccessException("not a simulation")
    }

    public Collection prepareData() {
        return null
    }

    public Map getParameters() {
        [
                "LoBReportBruttoCDF0": getLoBBruttoCDF(0),
                "LoBReportBruttoTable0": getLoBBruttoTable(0),
                "TotalBusinessCDF0": getTotalBusinessCDF(0),
                "ExpectedLossStackedBar0": getExpectedLossStackedBar(0),
                "WaterfallClaimsGross0": JasperChartUtils.generateWaterfallChart(getGrossVar99ForWaterfallChart(0)),
                "WaterfallClaimsNet0": JasperChartUtils.generateWaterfallChart(getNetVar99ForWaterfallChart(0)),
                "PremLossOverviewChart": getPremLossOverviewChart(0),
                "PremLossOverviewTable": getPremLossOverviewTable(0),

        ]
    }

    protected JRBeanCollectionDataSource getPremLossOverviewTable(int periodIndex) {
        Collection currentValues = new ArrayList<PremLossOverviewTableBean>()

        lobName.each {
            PremLossOverviewTableBean bean = new PremLossOverviewTableBean(lineOfBusiness: it)

            String path = grossPremiumPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, grossPremiumPathMap[it][1], grossPremiumPathMap[it][2])
            bean.premiumGrossMean = mean / divider

            path = netPremiumPathMap[it][0]
            mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPremiumPathMap[it][1], netPremiumPathMap[it][2])
            bean.premiumNetMean = mean / divider

            path = grossPathMap[it][0]
            mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, grossPathMap[it][1], grossPathMap[it][2])
            bean.claimsGrossMean = mean / divider

            path = netPathMap[it][0]
            mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPathMap[it][1], netPathMap[it][2])
            bean.claimsNetMean = mean / divider

            VarFunction var = new VarFunction(99, QuantilePerspective.LOSS)
            Double var99 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(grossPathMap[it]))
            bean.claimsGrossVar = var99 / divider

            var = new VarFunction(99, QuantilePerspective.LOSS)
            var99 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(netPathMap[it]))
            bean.claimsNetVar = var99 / divider

            currentValues << bean
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource

    }

    protected JRBeanCollectionDataSource getPremLossOverviewChart(int periodIndex) {
        Collection currentValues = new ArrayList<ExpectedLossChartDataBean>()

        lobName.each {
            String path = netPremiumPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPremiumPathMap[it][1], netPremiumPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "premium net", value: mean / divider)
        }

        lobName.each {
            String path = netPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPathMap[it][1], netPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "claims net mean", value: mean / divider)
        }

        lobName.each {
            VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
            Double var99 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(netPathMap[it]))
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "claims net VaR 99.5%", value: var99 / divider)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getLoBBruttoCDF(int periodIndex) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()
        Map dbValues = readDBValues(["CapitalEagle:mtpl:subRiProgram:outClaimsGross", collector, "ultimate"], periodIndex)
        double bandwidth = calcBandwidth(dbValues)
        List xyPairs = JEstimator.gaussKernelBandwidthCdf(dbValues["values"], bandwidth, false)

        lobName.each {String line ->
            dbValues = readDBValues(grossPathMap[line], periodIndex)
            xyPairs = JEstimator.gaussKernelBandwidthCdf(dbValues["values"], calcBandwidth(dbValues), false)
            xyPairs.each {List xyPair ->
                currentValues << new ReportChartDataBean(x: xyPair[0] / divider, y: xyPair[1], line: line, period: periodIndex)
            }
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getLoBBruttoTable(int periodIndex) {
        Collection currentValues = new ArrayList<LoBReportBruttoTableDataBean>()
        lobName.each {
            currentValues << createLoBReportBruttoTableDataBean(grossPathMap[it], periodIndex, it)
        }
        currentValues << createLoBReportBruttoTableDataBean(summaryPathMap["gross"], periodIndex, "total")
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getTotalBusinessCDF(int periodIndex) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()


        Map dbValues = readDBValues(summaryPathMap["gross"], periodIndex)
        double bandwidth = calcBandwidth(dbValues)
        List xyPairs = JEstimator.gaussKernelBandwidthCdf(dbValues["values"], bandwidth, false)
        xyPairs.each {List xyPair ->
            currentValues << new ReportChartDataBean(x: xyPair[0] / divider, y: xyPair[1], line: "gross", period: periodIndex)
        }

        dbValues = readDBValues(summaryPathMap["net"], periodIndex)
        bandwidth = calcBandwidth(dbValues)
        xyPairs = JEstimator.gaussKernelBandwidthCdf(dbValues["values"], bandwidth, false)
        xyPairs.each {List xyPair ->
            currentValues << new ReportChartDataBean(x: xyPair[0] / divider, y: xyPair[1], line: "net", period: periodIndex)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }



    protected LoBReportBruttoTableDataBean createLoBReportBruttoTableDataBean(List path, int periodIndex, String name) {
        double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2])
        PercentileFunction percentile = new PercentileFunction(95, QuantilePerspective.LOSS)
        Double per95 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        percentile = new PercentileFunction(99.5, QuantilePerspective.LOSS)
        Double per99 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        VarFunction var = new VarFunction(95, QuantilePerspective.LOSS)
        Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        var = new VarFunction(99.5, QuantilePerspective.LOSS)
        Double var99 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))

        return new LoBReportBruttoTableDataBean(lineOfBusiness: name, mean: mean / divider, percentile95: per95 / divider, percentile99: per99 / divider, var95: var95 / divider, var99: var99 / divider)
    }

    protected Map readDBValues(List path, int periodIndex) {
        Map map = [:]
        map["stdDev"] = ResultAccessor.getStdDev(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2])

        PercentileFunction percentile = new PercentileFunction(75, QuantilePerspective.LOSS)
        Double per75 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        percentile = new PercentileFunction(25, QuantilePerspective.LOSS)
        Double per25 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        map["IQR"] = per75 - per25

        map["values"] = ResultAccessor.getValues(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2]).sort()
        map["n"] = map["values"].size()
        return map
    }

    protected double calcBandwidth(Map values) {
        return JEstimator.calcBandwidthForGaussKernelEstimate(values["stdDev"], values["IQR"], values["n"])
    }

    public static List propertyValueTypes = ["storm", "eq", "flood"]

    protected JRBeanCollectionDataSource getExpectedLossStackedBar(int periodIndex) {
        Collection currentValues = new ArrayList<ExpectedLossChartDataBean>()

        lobName.each {
            String path = singleClaimsPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, singleClaimsPathMap[it][1], singleClaimsPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "single", value: mean / divider)
        }

        lobName.each {
            String path = attritionalClaimsPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, attritionalClaimsPathMap[it][1], attritionalClaimsPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "attritional", value: mean / divider)
        }

        propertyValueTypes.each {
            String path = morePropertyClaimsPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, morePropertyClaimsPathMap[it][1], morePropertyClaimsPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: "property", claimsType: it, value: mean / divider)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected List<ReportWaterfallDataBean> getGrossVar99ForWaterfallChart(int periodIndex) {
        List<ReportWaterfallDataBean> beans = []

        lobName.each {String name ->
            VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
            Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(grossPathMap[name]))
            beans << new ReportWaterfallDataBean(line: name, value: var95 / divider)
        }

        beans.sort()
        beans = beans.reverse()

        VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
        Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(summaryPathMap["gross"]))
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: var95 / divider)

        double sum = beans.value.sum()
        ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: total.value - sum)

        beans << div
        beans << total
        return beans
    }

    protected List<ReportWaterfallDataBean> getNetVar99ForWaterfallChart(int periodIndex) {
        List<ReportWaterfallDataBean> beans = []

        lobName.each {String name ->
            VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
            Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(netPathMap[name]))
            beans << new ReportWaterfallDataBean(line: name, value: var95 / divider)
        }

        beans.sort()
        beans = beans.reverse()

        VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
        Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(summaryPathMap["net"]))
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: var95 / divider)

        double sum = beans.value.sum()
        ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: total.value - sum)

        beans << div
        beans << total
        return beans
    }

    public String getReportFileName() {
        "CapitalEagleReports"
    }
}