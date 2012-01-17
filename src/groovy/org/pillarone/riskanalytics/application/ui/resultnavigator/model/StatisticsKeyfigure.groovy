package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import org.pillarone.riskanalytics.core.output.PostSimulationCalculation

/**
 * @author martin.melchior
 */
public enum StatisticsKeyfigure {
    MEAN(PostSimulationCalculation.MEAN),
    STDEV(PostSimulationCalculation.STDEV),
    MIN(PostSimulationCalculation.MIN),
    MAX(PostSimulationCalculation.MAX),
    PERCENTILE(PostSimulationCalculation.PERCENTILE),
    VAR(PostSimulationCalculation.VAR),
    TVAR(PostSimulationCalculation.TVAR),
    PERCENTILE_PROFIT(PostSimulationCalculation.PERCENTILE_PROFIT),
    VAR_PROFIT(PostSimulationCalculation.VAR_PROFIT),
    TVAR_PROFIT(PostSimulationCalculation.TVAR_PROFIT),
    ITERATION("Iteration");
    
    String name

    private StatisticsKeyfigure(String name) {
        this.name = name
    }

    boolean needsParameters() {
        switch (this) {
            case MEAN:
            case STDEV:
            case MIN:
            case MAX:
                return false
            default:
                return true
        }
    }
    
    static List<String> getNames() {
        List<String> names = []
        for (StatisticsKeyfigure kf : StatisticsKeyfigure.values()) {
            names.add(kf.name)
        }
        return names;
    }
    
    static StatisticsKeyfigure getEnumValue(String name) {
        return values().find { it -> it.name.equalsIgnoreCase(name)}
    }
}