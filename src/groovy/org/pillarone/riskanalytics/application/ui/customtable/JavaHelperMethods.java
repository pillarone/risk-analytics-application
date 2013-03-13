package org.pillarone.riskanalytics.application.ui.customtable;

import org.nfunk.jep.ParseException;
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor;
import org.pillarone.riskanalytics.core.dataaccess.ResultPathDescriptor;
import org.pillarone.riskanalytics.core.output.SimulationRun;

import java.util.List;

/**
*   author simon.parten @ art-allianz . com
 */
public class JavaHelperMethods {

    public static void checkSimulationRun( StringBuilder stringBuilder, SimulationRun run, String simRunName  ){
        if(run == null) {
            stringBuilder.append("Simulation: '");
            stringBuilder.append(simRunName);
            stringBuilder.append("' doesn't exist. Please check.");
        }
    }

    public static void checkPeriod(Number period, StringBuilder stringBuilder ){
        if(period.doubleValue() < 1 ) {
            stringBuilder.append("period argument: ");
            stringBuilder.append(period);
            stringBuilder.append(" not allowed, strictly +ve only");
        }
    }

    public static void checkIteration(Number iteration, StringBuilder stringBuilder, SimulationRun run ){
        if(iteration.intValue() > run.getIterations() ) {
            stringBuilder.append("Iteration: ");
            stringBuilder.append(iteration);
            stringBuilder.append(" requested. More than iterations in sim: ");
            stringBuilder.append(run.getIterations());
        }
    }

    public static void checkModelContextInfo(Number period, StringBuilder stringBuilder, SimulationRun simulationRun, String pathName, String fieldName, String collectorName ) throws ParseException {
        if(simulationRun != null){
            if(period.intValue() > simulationRun.getPeriodCount()) {
                stringBuilder.append(" Period argument: ");
                stringBuilder.append(period);
                stringBuilder.append(" greater than max periods : ");
                stringBuilder.append(simulationRun.getPeriodCount());
            }
            List<ResultPathDescriptor> resultPaths = ResultAccessor.getDistinctPaths(simulationRun);
            String foundResultPath = null;
            String withField = null;
            boolean foundCollector = false;
            for (ResultPathDescriptor resultPath : resultPaths) {
                if(resultPath.getPath().getPathName().equals(pathName)) {
                    foundResultPath = pathName;
                    if(resultPath.getField().getFieldName().equals(fieldName)) {
                        withField = fieldName;

                        if(resultPath.getCollector().getCollectorName().equals(collectorName)) {
                            foundCollector = true;
                            foundResultPath = null;
                            withField = null;
                            break;
                        }
                    }
                }
            }
            if(! (foundResultPath == null && withField == null && foundCollector  ) ) {
                if(foundResultPath == null) {
                    stringBuilder.append("Failed to find path - "  );
                    stringBuilder.append(pathName);
                    throw new ParseException(stringBuilder.toString());
                }
                if(withField == null) {
                    stringBuilder.append("Path found, field not found - ");
                    stringBuilder.append(fieldName);
                    throw new ParseException(stringBuilder.toString());
                }
                if(!foundCollector) {
                    stringBuilder.append("Path and field found, collector not found - ");
                    stringBuilder.append(collectorName);
                    throw new ParseException(stringBuilder.toString());
                }
            }
        }

    }
}
