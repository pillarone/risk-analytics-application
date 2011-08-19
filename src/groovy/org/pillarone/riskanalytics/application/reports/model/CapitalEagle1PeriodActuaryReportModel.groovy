package org.pillarone.riskanalytics.application.reports.model

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.reports.CapitalEagleReportModel
import org.pillarone.riskanalytics.application.reports.bean.LoBMeanStdDevIQRPerDataBean
import org.pillarone.riskanalytics.application.reports.bean.LoBReportBruttoTableDataBean
import org.pillarone.riskanalytics.application.reports.bean.PropertyValuePairBean
import org.pillarone.riskanalytics.application.reports.bean.ReportChartDataBean
import org.pillarone.riskanalytics.application.util.JEstimator
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class CapitalEagle1PeriodActuaryReportModel extends CapitalEagleReportModel {

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

    public CapitalEagle1PeriodActuaryReportModel(Simulation simulation) {
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
                "LoBRatioNetPdf0": getLoBRatioNetPdf(0),
                "LoBRatioGrossPdf0": getLoBRatioGrossPdf(0),
                "PremiumNet0": getPremiumNetPie(0),
                "PremiumGross0": getPremiumGrossPie(0),
                "grossKeyFigureSummary": getKeyFigureSummary(0, grossPathMap),
                "netKeyFigureSummary": getKeyFigureSummary(0, netPathMap)

        ]
    }



    protected JRBeanCollectionDataSource getKeyFigureSummary(int periodIndex, Map lines) {

        Collection currentValues = new ArrayList<LoBReportBruttoTableDataBean>()
        lobName.each {
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, lines[it][0], lines[it][1], lines[it][2])
            double stdDev = ResultAccessor.getStdDev(simulation.getSimulationRun(), periodIndex, lines[it][0], lines[it][1], lines[it][2])

            PercentileFunction percentile = new PercentileFunction(25, QuantilePerspective.LOSS)
            Double per25 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(lines[it]))

            percentile = new PercentileFunction(75, QuantilePerspective.LOSS)
            Double per75 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(lines[it]))

            double IQR = per75 - per25

            percentile = new PercentileFunction(99.5, QuantilePerspective.LOSS)
            Double per995 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(lines[it]))

            currentValues << new LoBMeanStdDevIQRPerDataBean(lineOfBusiness: it, mean: mean, stdDev: stdDev, IQR: IQR, per995: per995)
        }
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getPremiumNetPie(int periodIndex) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()

        lobName.each {
            String path = netPremiumPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, netPremiumPathMap[it][1], netPremiumPathMap[it][2])
            currentValues << new PropertyValuePairBean(property: it, value: mean / divider)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getPremiumGrossPie(int periodIndex) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()

        lobName.each {
            String path = grossPathMap[it][0]
            double mean = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path, grossPathMap[it][1], grossPathMap[it][2])
            currentValues << new PropertyValuePairBean(property: it, value: mean / divider)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getLoBRatioNetPdf(int periodIndex) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()

        lobName.each {String line ->
            Map dbValues = readDBValues(netPathMap[line], periodIndex)
            List xyPairs = JEstimator.gaussKernelBandwidthPdf(dbValues["values"], calcBandwidth(dbValues), false)
            xyPairs.each {List xyPair ->
                currentValues << new ReportChartDataBean(x: xyPair[0] * 100 / dbValues["mean"], y: xyPair[1], line: line, period: periodIndex)
            }
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected JRBeanCollectionDataSource getLoBRatioGrossPdf(int periodIndex) {
        Collection currentValues = new ArrayList<ReportChartDataBean>()

        lobName.each {String line ->
            Map dbValues = readDBValues(grossPathMap[line], periodIndex)
            List xyPairs = JEstimator.gaussKernelBandwidthPdf(dbValues["values"], calcBandwidth(dbValues), false)
            xyPairs.each {List xyPair ->
                currentValues << new ReportChartDataBean(x: xyPair[0] * 100 / dbValues["mean"], y: xyPair[1], line: line, period: periodIndex)
            }
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    protected double calcBandwidth(Map values) {
        return JEstimator.calcBandwidthForGaussKernelEstimate(values["stdDev"], values["IQR"], values["n"])
    }



    public String getReportFileName() {
        "CapitalEagleActuaryReport"
    }
}