package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.CollectorMapping

/**
 *
 */
class ResultAccess {

    Map<Long, String> pathCache = [:]
    Map<Long, String> fieldCache = [:]

    static List<SimulationRun> getSimulationRuns() {
        SimulationRun.withTransaction {
            return SimulationRun.findAll()
        }
    }

    static List getSimulationRuns(Model model) {
        Class clazz = model.class
        return getSimulationRuns(clazz)
    }

    static List getSimulationRuns(Class modelClass) {
        SimulationRun.withTransaction {
            return SimulationRun.findAllByModel(modelClass.name)
        }
    }

    public List<OutputElement> getOutputElements(SimulationRun run) {
        StringBuilder builder = new StringBuilder(System.getProperty("user.home"));
        builder.append(File.separatorChar);
        builder.append(".pillarone");
        builder.append(File.separatorChar);
        builder.append("RiskAnalytics-1.4-BETA-8");
        builder.append(File.separatorChar);
        builder.append("database");
        String location = builder.toString() + File.separator + "simulations" + File.separator + run.id
        File file = new File(location)
        List<OutputElement> result = []
        for (File f in file.listFiles()) {
            String[] ids = f.name.split("_")
            long pathId = Long.parseLong(ids[0])
            long fieldId = Long.parseLong(ids[2])
            long collectorId = Long.parseLong(ids[3])
            String path = PathMapping.findById(pathId)?.getPathName()
            String field = FieldMapping.findById(fieldId)?.getFieldName()
            String collector = CollectorMapping.findById(collectorId)?.getCollectorName()
            OutputElement element = new OutputElement(path: path, field: field, collector: collector)
            element.addCategoryValue(OutputElement.PATH, path)
            element.addCategoryValue(OutputElement.FIELD, field)
            element.addCategoryValue(OutputElement.COLLECTOR, collector)
            if (!result.contains(element)) {
                result.add(element)
            }
        }
        return result
    }
}
