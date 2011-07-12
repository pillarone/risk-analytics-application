package org.pillarone.riskanalytics.application.reports.gira.model

import org.pillarone.riskanalytics.application.reports.bean.ReportWaterfallDataBean
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewUtils
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction
import org.pillarone.riskanalytics.application.dataaccess.function.AbstractFunction
import org.pillarone.riskanalytics.application.reports.gira.action.ResultPathParser
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun
import sun.security.jca.GetInstance
import org.pillarone.riskanalytics.application.reports.gira.action.PathType

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractWaterfallChart {

    protected ResultPathParser parser
    protected Simulation simulation
    protected AbstractFunction function

    protected List<ReportWaterfallDataBean> getBeans(List<String> paths) {
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
                    Double var95 = function.evaluate(simulation.getSimulationRun(), periodIndex, ResultViewUtils.createRTTN(path, AbstractReportModel.collector, "ultimate"))
                    sum += var95
                    beans << new ReportWaterfallDataBean(line: ResultViewUtils.getResultNodePathShortDisplayName(simulation?.modelClass, path), value: var95)
                } catch (Exception ex) {}
            }

        }

        totalValue = function.evaluate(simulation.getSimulationRun(), periodIndex, ResultViewUtils.createRTTN(parent, AbstractReportModel.collector, "ultimate"))
        addDivDataBean(beans, totalValue - sum)
        addTotalDataBean(beans, totalValue)
        return beans
    }

    protected void addTotalDataBean(List<ReportWaterfallDataBean> beans, Double totalValue) {
        ReportWaterfallDataBean total = new ReportWaterfallDataBean(line: "total", value: totalValue)
        beans << total
    }

    protected void addDivDataBean(List<ReportWaterfallDataBean> beans, Double divValue) {
        try {
            ReportWaterfallDataBean div = new ReportWaterfallDataBean(line: "diversification", value: divValue)
            beans << div
        } catch (Exception ex) {}
    }

    protected abstract AbstractFunction getFunction()

    public static AbstractWaterfallChart getInstance(PathType pathType, Simulation simulation, ResultPathParser parser) {
        AbstractWaterfallChart instance
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: instance = new ClaimsWaterfallChart(); break
            case PathType.REINSURANCE: instance = new ReinsuranceWaterfallChart(); break
        }
        instance.simulation = simulation
        instance.parser = parser
        return instance
    }

    public static String getTitle(PathType pathType){
         switch (pathType) {
            case PathType.CLAIMSGENERATORS: return "Overview  VaR 99.5% (ultimate)"
            case PathType.REINSURANCE: return "Overview  Mean (ultimate)"
        }
    }
}
