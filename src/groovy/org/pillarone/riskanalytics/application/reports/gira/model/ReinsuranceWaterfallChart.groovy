package org.pillarone.riskanalytics.application.reports.gira.model

import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.dataaccess.function.AbstractFunction
import org.pillarone.riskanalytics.application.reports.bean.ReportWaterfallDataBean

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReinsuranceWaterfallChart extends AbstractWaterfallChart {

    protected AbstractFunction getFunction() {
        if (!this.@function)
            this.@function = new MeanFunction()
        return this.@function
    }

    @Override
    protected void addDivDataBean(List<ReportWaterfallDataBean> beans, Double divValue) {

    }


}
