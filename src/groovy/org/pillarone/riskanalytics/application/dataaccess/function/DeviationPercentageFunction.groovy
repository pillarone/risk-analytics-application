package org.pillarone.riskanalytics.application.dataaccess.function


class DeviationPercentageFunction extends AbstractComparisonFunction {

    public static final String DEVIATION_PERCENTAGE = "DevPercentage"

    String getName() {
        return DEVIATION_PERCENTAGE
    }

    @Override
    protected double evaluateComparison(double referenceResult, double resultToCompare) {
        return ((resultToCompare - referenceResult) / referenceResult) * 100
    }


}
