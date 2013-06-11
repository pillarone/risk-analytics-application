package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic


@CompileStatic
class DeviationAbsoluteFunction extends AbstractComparisonFunction {


    public static final String ABSOLUTE_DIFFERENCE = "DevAbsoluteDifference"

    String getName() {
        return ABSOLUTE_DIFFERENCE
    }

    @Override
    protected double evaluateComparison(double referenceResult, double resultToCompare) {
        return resultToCompare - referenceResult
    }


}
