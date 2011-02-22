package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import java.text.NumberFormat
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy


abstract class ResultViewUtils {

    public static List<String> obtainAllPaths(ConfigObject paths) {
        List res = []
        for (Map.Entry<String, ConfigObject> entry in paths.entrySet()) {
            String path = entry.key + ":"
            for (String field in entry.value.keySet()) {
                res << path + field
            }
        }
        return res
    }

/**
 * Loads all PostSimulationCalculations of a simulation and stores them in a map.
 * This is faster than creating a query for every cell when the result is needed.
 */
    public static ConfigObject initPostSimulationCalculations(SimulationRun simulationRun) {
        NumberFormat numberFormat = NumberFormat.getInstance()
        ConfigObject results = new ConfigObject()

        List<Object[]> calculations = PostSimulationCalculation.executeQuery("SELECT period, path.pathName, field.fieldName, keyFigure, keyFigureParameter, result FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p " +
                " WHERE p.run.id = ? order by p.keyFigureParameter asc", [simulationRun.id])
        for (Object[] psc in calculations) {
            Map periodMap = results[psc[0].toString()]
            Map pathMap = periodMap[psc[1]]
            Map fieldMap = pathMap[psc[2]]
            Map keyFigureMap = fieldMap[psc[3]]
            BigDecimal keyFigureParameter = psc[4]
            String param = keyFigureParameter != null ? numberFormat.format(keyFigureParameter) : "null"
            if (!keyFigureMap.containsKey(param)) {
                keyFigureMap[param] = psc[5]
            }
        }

        return results
    }


    public static Map<String, ICollectingModeStrategy> obtainsCollectors(SimulationRun simulationRun, List allPaths) {
        Map<String, ICollectingModeStrategy> result = [:]
        List<Object[]> calculations = PostSimulationCalculation.executeQuery("SELECT path.pathName, field.fieldName, collector.collectorName FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p " +
                " WHERE p.run.id = ?", [simulationRun.id])
        for (Object[] psc in calculations) {
            String path = "${psc[0]}:${psc[1]}"
            String collector = psc[2]
            if (allPaths.contains(path)) {
                result.put(path, CollectingModeFactory.getStrategy(collector))
            }
        }

        return result
    }

}
