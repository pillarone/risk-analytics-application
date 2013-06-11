package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic


@CompileStatic
class FractionPercentageFunction extends AbstractComparisonFunction {

    public static final String FRACTION_PERCENTAGE = "FrPercentage"

    String getName() {
        return FRACTION_PERCENTAGE
    }

    @Override
    protected double evaluateComparison(double referenceResult, double resultToCompare) {
        return (resultToCompare / referenceResult) * 100
    }


}
