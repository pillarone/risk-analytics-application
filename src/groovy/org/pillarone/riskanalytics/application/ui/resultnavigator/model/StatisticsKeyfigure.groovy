package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import org.pillarone.riskanalytics.core.output.PostSimulationCalculation

/**
 * An enumeration for the statistics key figures.
 * Each element also provides the information whether a parameter is needed.
 *
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
    ITERATION("Iteration");
    
    String name

    private StatisticsKeyfigure(String name) {
        this.name = name
    }

    /**
     * Returns whether this given enumeration needs a parameter
     * @return
     */
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