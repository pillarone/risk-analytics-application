package org.pillarone.riskanalytics.application.ui.customtable

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

/**
*   author simon.parten @ art-allianz . com
 */
public class GroovyHelperMethods {
    /* SCP 13.03.2013 - Used when java classes want to get stuff out the database. SHould this be in core?  */
    public static SimulationRun findSimulationRun(String simName) {
        return SimulationRun.findByName(simName)
    }



    static double valuesAboveThreshold(SimulationRun simulationRun, int periodIndex = 0, String pathName, String collectorName, String fieldName, Double threshold) {
        List<Integer> iterations = 1 .. simulationRun.getIterations()
        Map<Integer, Double> values = ResultAccessor.getIterationConstrainedValues(simulationRun, periodIndex - 1, pathName, fieldName, collectorName, iterations)
        return  values.findAll {it -> it.getValue() > threshold  }.size()
    }
}
