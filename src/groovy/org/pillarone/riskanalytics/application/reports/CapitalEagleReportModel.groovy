package org.pillarone.riskanalytics.application.reports

import org.pillarone.riskanalytics.application.dataaccess.function.PercentileFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.simulation.item.Simulation

public abstract class CapitalEagleReportModel implements ReportModel {

    public static double divider = 1000
    Simulation simulation
    public static String collector = AggregatedCollectingModeStrategy.IDENTIFIER
    public static List lobName = ["mtpl", "motor hull", "personal accident", "property"]
    public static Map grossPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subRiProgram:outClaimsGross", collector, "ultimate"],
            "motor hull": ["CapitalEagle:motorHull:subRiProgram:outClaimsGross", collector, "ultimate"],
            "personal accident": ["CapitalEagle:personalAccident:subRiProgram:outClaimsGross", collector, "ultimate"],
            "property": ["CapitalEagle:property:subRiProgram:outClaimsGross", collector, "ultimate"],
    ]
    public static Map netPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subRiProgram:outClaimsNet", collector, "ultimate"],
            "motor hull": ["CapitalEagle:motorHull:subRiProgram:outClaimsNet", collector, "ultimate"],
            "personal accident": ["CapitalEagle:personalAccident:subRiProgram:outClaimsNet", collector, "ultimate"],
            "property": ["CapitalEagle:property:subRiProgram:outClaimsNet", collector, "ultimate"],
    ]

    public static Map singleClaimsPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subClaimsGenerator:subSingleClaimsGenerator:outClaims", collector, "ultimate"],
            "motor hull": ["CapitalEagle:motorHull:subClaimsGenerator:subSingleClaimsGenerator:outClaims", collector, "ultimate"],
            "personal accident": ["CapitalEagle:personalAccident:subClaimsGenerator:subSingleClaimsGenerator:outClaims", collector, "ultimate"],
            "property": ["CapitalEagle:property:subClaimsGenerator:subSingleClaimsGenerator:outClaims", collector, "ultimate"],
    ]

    public static Map attritionalClaimsPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subClaimsGenerator:subAttritionalClaimsGenerator:outClaims", collector, "ultimate"],
            "motor hull": ["CapitalEagle:motorHull:subClaimsGenerator:subAttritionalClaimsGenerator:outClaims", collector, "ultimate"],
            "personal accident": ["CapitalEagle:personalAccident:subClaimsGenerator:subAttritionalClaimsGenerator:outClaims", collector, "ultimate"],
            "property": ["CapitalEagle:property:subClaimsGenerator:subAttritionalSeverityClaimsGenerator:outClaims", collector, "ultimate"],
    ]

    public static Map grossPremiumPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subRiProgram:outUnderwritingInfoGross", collector, "premium"],
            "motor hull": ["CapitalEagle:motorHull:subRiProgram:outUnderwritingInfoGross", collector, "premium"],
            "personal accident": ["CapitalEagle:personalAccident:subRiProgram:outUnderwritingInfoGross", collector, "premium"],
            "property": ["CapitalEagle:property:subRiProgram:outUnderwritingInfoGross", collector, "premium"],
    ]

    public static Map netPremiumPathMap = [
            "mtpl": ["CapitalEagle:mtpl:subRiProgram:outUnderwritingInfoNet", collector, "premium"],
            "motor hull": ["CapitalEagle:motorHull:subRiProgram:outUnderwritingInfoNet", collector, "premium"],
            "personal accident": ["CapitalEagle:personalAccident:subRiProgram:outUnderwritingInfoNet", collector, "premium"],
            "property": ["CapitalEagle:property:subRiProgram:outUnderwritingInfoNet", collector, "premium"],
    ]

    public static Map summaryPathMap = [
            "gross": ["CapitalEagle:Summary:claimsAggregator:outClaimsGross", collector, "ultimate"],
            "net": ["CapitalEagle:Summary:claimsAggregator:outClaimsNet", collector, "ultimate"],
            "ceded": ["CapitalEagle:Summary:claimsAggregator:outClaimsCeded", collector, "ultimate"],
    ]

    public static Map morePropertyClaimsPathMap = [
            "storm": ["CapitalEagle:property:subClaimsGenerator:subStormGenerator:outClaims", collector, "ultimate"],
            "eq": ["CapitalEagle:property:subClaimsGenerator:subEQGenerator:outClaims", collector, "ultimate"],
            "flood": ["CapitalEagle:property:subClaimsGenerator:subFloodGenerator:outClaims", collector, "ultimate"],
    ]

    protected validatePathMap(List paths, Map m) {
        m.each {
            if (!paths.contains(it.value[0])) {
                throw new IllegalArgumentException("This report requires data which is not been available in the result.\n"
                        + "Please use a result template containing 'report' in the name.\n\nMissing Paths:\n${it.value[0]}")
            }
        }
    }

    protected ResultTableTreeNode createRTTN(List values) {
        ResultTableTreeNode node = new ResultTableTreeNode("")
        node.resultPath = "${values[0]}:${values[2]}"
        node.collector = values[1]
        return node
    }

    protected Map readDBValues(List path, int periodIndex) {
        Map map = [:]
        map["stdDev"] = ResultAccessor.getStdDev(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2])
        map["mean"] = ResultAccessor.getMean(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2])

        PercentileFunction percentile = new PercentileFunction(75, QuantilePerspective.LOSS)
        Double per75 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        percentile = new PercentileFunction(25, QuantilePerspective.LOSS)
        Double per25 = percentile.evaluate(simulation.getSimulationRun(), periodIndex, createRTTN(path))
        map["IQR"] = per75 - per25

        map["values"] = ResultAccessor.getValues(simulation.getSimulationRun(), periodIndex, path[0], path[1], path[2]).sort()
        map["n"] = map["values"].size()
        return map
    }
}