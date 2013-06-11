package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic


@CompileStatic
class FractionAbsoluteFunction extends AbstractComparisonFunction {

    public static final String FRACTION_ABSOLUTE = "FrAbsoluteDifference"

    String getName() {
        return FRACTION_ABSOLUTE
    }

    @Override
    protected double evaluateComparison(double referenceResult, double resultToCompare) {
        return (resultToCompare - referenceResult) / referenceResult
    }


}
