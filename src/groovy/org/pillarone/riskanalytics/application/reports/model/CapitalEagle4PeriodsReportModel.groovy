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

class CapitalEagle4PeriodsReportModel extends CapitalEagleReportModel {

    boolean showPeriodLabels = true

    protected boolean validateSimulation() {
        if (simulation.modelClass.simpleName != "CapitalEagleModel") {
            throw new IllegalArgumentException("wrong model")
        }
        if (simulation.periodCount != 4) {
            throw new IllegalArgumentException("not 4 periods")
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


    public CapitalEagle4PeriodsReportModel(Simulation simulation) {
        this.simulation = simulation
        if (!simulation.isLoaded()) simulation.load()
        validateSimulation()
    }

    public CapitalEagle4PeriodsReportModel(Object object) {
        throw new IllegalAccessException("not a simulation")
    }

    public String getPeriodLabel(int periodIndex) {
        List labels = ["Peak Risk", "NP Cover", "MTPL QS, NP Cover", "QS and NP Cover"]
        return labels[periodIndex]
    }

    public Collection prepareData() {
        return null
    }

    public Map getParameters() {
        [
                "LoBReportBruttoCDF0": getLoBBruttoCDF(0),
                "LoBReportBruttoCDF1": getLoBBruttoCDF(1),
                "LoBReportBruttoCDF2": getLoBBruttoCDF(2),
                "LoBReportBruttoCDF3": getLoBBruttoCDF(3),
                "LoBReportBruttoTable0": getLoBBruttoTable(0),
                "LoBReportBruttoTable1": getLoBBruttoTable(1),
                "LoBReportBruttoTable2": getLoBBruttoTable(2),
                "LoBReportBruttoTable3": getLoBBruttoTable(3),
                "TotalBusinessCDF0": getTotalBusinessCDF(0),
                "ExpectedLossStackedBar0": getExpectedLossStackedBar(0),
                "WaterfallClaimsGross0": JasperChartUtils.generateWaterfallChart(getGrossVar99ForWaterfallChart(0)),
                "WaterfallClaimsGross1": JasperChartUtils.generateWaterfallChart(getGrossVar99ForWaterfallChart(1)),
                "WaterfallClaimsGross2": JasperChartUtils.generateWaterfallChart(getGrossVar99ForWaterfallChart(2)),
                "WaterfallClaimsGross3": JasperChartUtils.generateWaterfallChart(getGrossVar99ForWaterfallChart(3)),
                "WaterfallClaimsNet0": JasperChartUtils.generateWaterfallChart(getNetVar99ForWaterfallChart(0)),
                "WaterfallClaimsNet1": JasperChartUtils.generateWaterfallChart(getNetVar99ForWaterfallChart(1)),
                "WaterfallClaimsNet2": JasperChartUtils.generateWaterfallChart(getNetVar99ForWaterfallChart(2)),
                "WaterfallClaimsNet3": JasperChartUtils.generateWaterfallChart(getNetVar99ForWaterfallChart(3)),
                "summary": getSummary(0),

                "overview0": getPremLossOverviewChart(0),
                "overview1": getPremLossOverviewChart(1),
                "overview2": getPremLossOverviewChart(2),
                "overview3": getPremLossOverviewChart(3),
                "LoBClaimsVaRTableBean": getLoBClaimsVarTable(),
                "NetClaimsMtpl": getNetClaimsGraph("mtpl"),
                "netClaimsTableMtpl": getNetClaimsTable("mtpl"),
                "netClaimsMotorHull": getNetClaimsGraph("motor hull"),
                "netClaimsTableMotorHull": getNetClaimsTable("motor hull"),
                "netClaimsPersonalAccident": getNetClaimsGraph("personal accident"),
                "netClaimsTablePersonalAccident": getNetClaimsTable("personal accident"),
                "netClaimsProperty": getNetClaimsGraph("property"),
                "netClaimsTableProperty": getNetClaimsTable("property")
        ]
    }

    protected JRBeanCollectionDataSource getLoBClaimsVarTable() {
        Collection currentValues = new ArrayList<VaRClaimsTableBean>()

        lobName.each {
            VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
            double grossVar = var.evaluate(simulation.getSimulationRun(), 0, createRTTN(grossPathMap[it]))
            double rv0 = var.evaluate(simulation.getSimulationRun(), 0, createRTTN(netPathMap[it]))
            double rv1 = var.evaluate(simulation.getSimulationRun(), 1, createRTTN(netPathMap[it]))
            double rv2 = var.evaluate(simulation.getSimulationRun(), 2, createRTTN(netPathMap[it]))
            double rv3 = var.evaluate(simulation.getSimulationRun(), 3, createRTTN(netPathMap[it]))

            currentValues << new VaRClaimsTableBean(lineOfBusiness: it, gross: grossVar, rv0: rv0, rv1: rv1, rv2: rv2, rv3: rv3)
        }

        double grossDiv = getGrossVar99ForWaterfallChart(0).find {it.line == "diversification"}.value * (-1000)
        double rv0Div = getNetVar99ForWaterfallChart(0).find {it.line == "diversification"}.value * (-1000)
        double rv1Div = getNetVar99ForWaterfallChart(1).find {it.line == "diversification"}.value * (-1000)
        double rv2Div = getNetVar99ForWaterfallChart(2).find {it.line == "diversification"}.value * (-1000)
        double rv3Div = getNetVar99ForWaterfallChart(3).find {it.line == "diversification"}.value * (-1000)
        currentValues << new VaRClaimsTableBean(lineOfBusiness: "diversification", gross: grossDiv, rv0: rv0Div, rv1: rv1Div, rv2: rv2Div, rv3: rv3Div)

        double grossTot = getGrossVar99ForWaterfallChart(0).find {it.line == "total"}.value * (1000)
        double rv0Tot = getNetVar99ForWaterfallChart(0).find {it.line == "total"}.value * (1000)
        double rv1Tot = getNetVar99ForWaterfallChart(1).find {it.line == "total"}.value * (1000)
        double rv2Tot = getNetVar99ForWaterfallChart(2).find {it.line == "total"}.value * (1000)
        double rv3Tot = getNetVar99ForWaterfallChart(3).find {it.line == "total"}.value * (1000)
        currentValues << new VaRClaimsTableBean(lineOfBusiness: "total", gross: grossTot, rv0: rv0Tot, rv1: rv1Tot, rv2: rv2Tot, rv3: rv3Tot)

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getNetClaimsTable(String lob) {
        Collection currentValues = new ArrayList<AllRVTableDataBean>()

        String type = "mean"
        double gross = ResultAccessor.getMean(simulation.getSimulationRun(), 0, grossPathMap[lob][0], grossPathMap[lob][1], grossPathMap[lob][2])
        double rv0 = ResultAccessor.getMean(simulation.getSimulationRun(), 0, grossPathMap[lob][0], grossPathMap[lob][1], grossPathMap[lob][2])
        double rv1 = ResultAccessor.getMean(simulation.getSimulationRun(), 1, grossPathMap[lob][0], grossPathMap[lob][1], grossPathMap[lob][2])
        double rv2 = ResultAccessor.getMean(simulation.getSimulationRun(), 2, grossPathMap[lob][0], grossPathMap[lob][1], grossPathMap[lob][2])
        double rv3 = ResultAccessor.getMean(simulation.getSimulationRun(), 3, grossPathMap[lob][0], grossPathMap[lob][1], grossPathMap[lob][2])
        currentValues << new AllRVTableDataBean(type: type, gross: gross, rv0: rv0, rv1: rv1, rv2: rv2, rv3: rv3)

        PercentileFunction percentile = new PercentileFunction(99.5, QuantilePerspective.LOSS)
        type = "99.5 %"
        gross = percentile.evaluate(simulation.getSimulationRun(), 0, createRTTN(grossPathMap[lob]))
        rv0 = percentile.evaluate(simulation.getSimulationRun(), 0, createRTTN(grossPathMap[lob]))
        rv1 = percentile.evaluate(simulation.getSimulationRun(), 1, createRTTN(grossPathMap[lob]))
        rv2 = percentile.evaluate(simulation.getSimulationRun(), 2, createRTTN(grossPathMap[lob]))
        rv3 = percentile.evaluate(simulation.getSimulationRun(), 3, createRTTN(grossPathMap[lob]))
        currentValues << new AllRVTableDataBean(type: type, gross: gross, rv0: rv0, rv1: rv1, rv2: rv2, rv3: rv3)

        VarFunction var = new VarFunction(99.5, QuantilePerspective.LOSS)
        type = "Var 99.5 %"
        gross = var.evaluate(simulation.getSimulationRun(), 0, createRTTN(grossPathMap[lob]))
        rv0 = var.evaluate(simulation.getSimulationRun(), 0, createRTTN(grossPathMap[lob]))
        rv1 = var.evaluate(simulation.getSimulationRun(), 1, createRTTN(grossPathMap[lob]))
        rv2 = var.evaluate(simulation.getSimulationRun(), 2, createRTTN(grossPathMap[lob]))
        rv3 = var.evaluate(simulation.getSimulationRun(), 3, createRTTN(grossPathMap[lob]))
        currentValues << new AllRVTableDataBean(type: type, gross: gross, rv0: rv0, rv1: rv1, rv2: rv2, rv3: rv3)

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getPremLossOverviewChart(int periodIndex) {
        Collection currentValues = new ArrayList<ExpectedLossChartDataBean>()

        lobName.each {
            String path = grossPremiumPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, grossPremiumPathMap[it][1], grossPremiumPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "premium gross", value: mean / divider)
        }

        lobName.each {
            String path = grossPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, grossPathMap[it][1], grossPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "claims gross", value: mean / divider)
        }

        lobName.each {
            String path = netPremiumPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPremiumPathMap[it][1], netPremiumPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "premium net", value: mean / divider)
        }

        lobName.each {
            String path = netPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPathMap[it][1], netPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "claims net", value: mean / divider)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getSummary(int periodIndex) {
        return getExpectedLossStackedBar(periodIndex)
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
            currentValues << new ReportChartDataBean(x: xyPair[0] / divider, y: xyPair[1], line: "total gross", period: periodIndex)
        }


        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getNetClaimsGraph(String lob) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()


        4.times {int periodIndex ->
            Map dbValues = readDBValues(netPathMap[lob], periodIndex)
            double bandwidth = calcBandwidth(dbValues)
            List xyPairs = JEstimator.gaussKernelBandwidthCdf(dbValues["values"], bandwidth, false)
            xyPairs.each {List xyPair ->
                currentValues << new ReportChartDataBean(x: xyPair[0] / divider, y: xyPair[1], line: getPeriodLabel(periodIndex), period: periodIndex)
            }
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }



    protected LoBReportBruttoTableDataBean createLoBReportBruttoTableDataBean(List paths, int periodIndex, String name) {
        double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, paths[0], paths[1], paths[2])
        PercentileFunction percentile = new PercentileFunction(95, QuantilePerspective.LOSS)
        Double per95 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(paths))
        percentile = new PercentileFunction(99, QuantilePerspective.LOSS)
        Double per99 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(paths))
        VarFunction var = new VarFunction(95, QuantilePerspective.LOSS)
        Double var95 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(paths))
        var = new VarFunction(99, QuantilePerspective.LOSS)
        Double var99 = var.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(paths))

        return new LoBReportBruttoTableDataBean(lineOfBusiness: name, mean: mean / divider, percentile95: per95 / divider, percentile99: per99 / divider, var95: var95 / divider, var99: var99 / divider)
    }


    protected double calcBandwidth(Map values) {
        return JEstimator.calcBandwidthForGaussKernelEstimate(values["stdDev"], values["IQR"], values["n"])
    }

    public static List propertyValueTypes = ["storm", "eq", "flood"]

    protected JRBeanCollectionDataSource getExpectedLossStackedBar(int periodIndex) {
        Collection currentValues = new ArrayList<ExpectedLossChartDataBean>()

        lobName.each {
            String path = attritionalClaimsPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, attritionalClaimsPathMap[it][1], attritionalClaimsPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "attritional", value: mean / divider)
        }

        lobName.each {
            String path = singleClaimsPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, singleClaimsPathMap[it][1], singleClaimsPathMap[it][2])
            currentValues << new ExpectedLossChartDataBean(line: it, claimsType: "single", value: mean / divider)
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

        return beans
    }

    public String getReportFileName() {
        "CapitalEagle4PeriodsReports"
    }
}