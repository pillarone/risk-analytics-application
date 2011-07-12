package org.pillarone.riskanalytics.application.reports.gira.model

import org.pillarone.riskanalytics.application.dataaccess.function.AbstractFunction
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.application.dataaccess.function.VarFunction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ClaimsWaterfallChart extends AbstractWaterfallChart {

    protected AbstractFunction getFunction() {
        if (!this.@function)
            this.@function = new VarFunction(99.5, QuantilePerspective.LOSS)
        return this.@function
    }


}
