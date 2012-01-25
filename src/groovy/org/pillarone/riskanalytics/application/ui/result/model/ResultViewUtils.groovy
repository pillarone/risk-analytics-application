package org.pillarone.riskanalytics.application.ui.result.model

import java.text.NumberFormat
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory

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
                " WHERE p.run.id = ? AND p.keyFigure != '${PostSimulationCalculation.PDF}'", [simulationRun.id])
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
        List<Object[]> calculations = PostSimulationCalculation.executeQuery("SELECT DISTINCT path.pathName, field.fieldName, collector.collectorName FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p " +
                " WHERE p.run.id = ? AND keyFigure != '${PostSimulationCalculation.PDF}'", [simulationRun.id])
        for (Object[] psc in calculations) {
            String path = "${psc[0]}:${psc[1]}"
            String collector = psc[2]
            if (allPaths.contains(path)) {
                result.put(path, CollectingModeFactory.getStrategy(collector))
            }
        }

        return result
    }

    public static Map<String, ICollectingModeStrategy> obtainsCollectors(List<SimulationRun> simulationRuns, List allPaths) {
        Map<String, ICollectingModeStrategy> result = [:]
        StringBuilder query = new StringBuilder("SELECT path.pathName, field.fieldName, collector.collectorName FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p where ")
        simulationRuns.eachWithIndex {SimulationRun simulationRun, int index ->
            query.append(" p.run.id = '" + simulationRun.id + "' ")
            if (index < simulationRuns.size() - 1)
                query.append(" or ")
        }
        List<Object[]> calculations = PostSimulationCalculation.executeQuery(query.toString())
        for (Object[] psc in calculations) {
            String path = "${psc[0]}:${psc[1]}"
            String collector = psc[2]
            if (allPaths.contains(path)) {
                result.put(path, CollectingModeFactory.getStrategy(collector))
            }
        }

        return result
    }

    /**
     * Loads all PostSimulationCalculations of a simulation and stores them in a map.
     * This is faster than creating a query for every cell when the result is needed.
     */
    public static ConfigObject initPostSimulationCalculations(List<SimulationRun> simulationRuns) {
        NumberFormat numberFormat = NumberFormat.getInstance()
        ConfigObject results = new ConfigObject()
        StringBuilder query = new StringBuilder("SELECT period, path.pathName, field.fieldName, keyFigure, keyFigureParameter, result FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p where ")
        simulationRuns.eachWithIndex {SimulationRun simulationRun, int index ->
            query.append(" p.run.id = '" + simulationRun.id + "' ")
            if (index < simulationRuns.size() - 1)
                query.append(" or ")
        }
        query.append(" order by p.keyFigureParameter asc")
        List<Object[]> calculations = PostSimulationCalculation.executeQuery(query.toString())
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

    public static String getResultNodePathDisplayName(Class modelClass, String path) {
        try {
            StringBuilder stringBuilder = new StringBuilder("")
            String[] pathNames = path.split(":")
            int index = 0
            String separator = " / "
            for (String nodeName: pathNames) {
                stringBuilder.append(I18NUtils.getResultStructureString(modelClass, nodeName, null))
                if (index++ != pathNames.size() - 1)
                    stringBuilder.append(separator)
            }
            return stringBuilder.toString()
        } catch (Exception ex) {
            return path
        }
    }

    public static String getResultNodesDisplayName(Class modelClass, String path) {
        try {
            StringBuilder stringBuilder = new StringBuilder("")
            String[] pathNames = path.split(":")
            String separator = " / "
            for (int i = 2; i < pathNames.length - 1; i++) {
                String nodeName = pathNames[i]
                stringBuilder.append(I18NUtils.getResultStructureString(modelClass, nodeName, null))
                if (i != pathNames.length - 2)
                    stringBuilder.append(separator)
            }
            return stringBuilder.toString()
        } catch (Exception ex) {
            return path
        }
    }

    public static String getResultNodePathShortDisplayName(Class modelClass, String path) {
        try {
            StringBuilder stringBuilder = new StringBuilder("")
            String[] pathNames = path.split(":")
            int index = 0
            String separator = " / "
            for (String nodeName: pathNames) {
                if (index > 1) {
                    stringBuilder.append(I18NUtils.getResultStructureString(modelClass, nodeName, null))
                    if (index != pathNames.size() - 1)
                        stringBuilder.append(separator)
                }
                index++
            }
            return stringBuilder.toString()
        } catch (Exception ex) {
            return path
        }
    }

    public static initPeriodLabels(SimulationRun simulationRun, Map periodLabels) {
        SimulationRun.withTransaction {status ->
            simulationRun = SimulationRun.get(simulationRun.id)
            Parameterization parameterization = ModellingItemFactory.getParameterization(simulationRun?.parameterization)
            parameterization.load(false)
            simulationRun.periodCount.times {int index ->
                periodLabels[index] = parameterization.getPeriodLabel(index)
            }
        }
    }

    public static ResultTableTreeNode createRTTN(String path, String collectorName, String fieldName) {
        ResultTableTreeNode node = new ResultTableTreeNode("")
        node.resultPath = "${path}:${fieldName}"
        node.collector = collectorName
        return node
    }


}
