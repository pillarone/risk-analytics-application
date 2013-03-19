package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.engine.grid.GridHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.util.PeriodLabelsUtil

/**
 * Helper class for accessing the meta information on the data included in the db or the file system,
 * in particular the information on what simulation runs are available, and what single value result entries
 * are available for a given simulation run (path, field, collector).
 *
 * @ martin.melchior
 */
class ResultAccess {

    /**
     * Returns all the simulation runs found in the db.
     * @return
     */
    static List<SimulationRun> getSimulationRuns() {
        SimulationRun.withTransaction {
            return SimulationRun.findAll()
        }
    }

    /**
     * Returns all the simulation runs found in the db for a given model
     * @param model
     * @return
     */
    static List getSimulationRuns(Model model) {
        Class clazz = model.class
        return getSimulationRuns(clazz)
    }

    /**
     * Returns all the simulation runs found in the db for a given model class
     * @param modelClass
     * @return
     */
    static List getSimulationRuns(Class modelClass) {
        SimulationRun.withTransaction {
            return SimulationRun.findAllByModel(modelClass.name)
        }
    }

    /**
     * Creates a list of OutputElement's for the given simulation run.
     * Only data is considered for which corresponding raw simulation data is found in the file system store.
     * These OutputElement's contain the necessary information on how to retrieve the data.
     * Category information is initialized with the path, field and collector information.
     * @param run
     * @return
     */
    public List<OutputElement> getOutputElements(SimulationRun run) {
        File file = new File(GridHelper.getResultLocation(run.id))
        List<OutputElement> result = []
        for (File f in file.listFiles()) {
            String[] ids = f.name.split("_")
            long pathId = Long.parseLong(ids[0])
            long fieldId = Long.parseLong(ids[2])
            long collectorId = Long.parseLong(ids[3])
            String path = PathMapping.findById(pathId)?.getPathName()
            String field = FieldMapping.findById(fieldId)?.getFieldName()
            String collector = CollectorMapping.findById(collectorId)?.getCollectorName()
            OutputElement element = new OutputElement(run: run, path: path, field: field, collector: collector, templatePath: new String(path))
            element.addCategoryValue(OutputElement.PATH, path)
            element.addCategoryValue(OutputElement.FIELD, field)
            element.addCategoryValue(OutputElement.COLLECTOR, collector)
            if (!result.contains(element)) {
                result.add(element)
            }
        }
        return result
    }

    public static List<String> getPeriodLabels(SimulationRun run) {
        Parameterization parameterization = ModellingItemFactory.getParameterization(run.parameterization)
        parameterization.load(false)
        Model model = parameterization.modelClass.newInstance()
        model.init()
        return PeriodLabelsUtil.getPeriodLabels(parameterization.periodLabels, run, model)
    }
}
