package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import java.text.SimpleDateFormat
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.grid.GridHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

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
        List<String> periodLabels = []
        // Whenever possible, use the saved period labels
        try {
            ParameterizationDAO dao = run.parameterization
            Parameterization parametrization = ModellingItemFactory.getParameterization(dao)
            if (parametrization && !parametrization.periodLabels.empty) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(DateFormatUtils.PARAMETER_DISPLAY_FORMAT)
                DateTimeFormatter parser = DateTimeFormat.forPattern(Parameterization.PERIOD_DATE_FORMAT)
                periodLabels = parametrization.periodLabels.collect { String it ->
                    try {
                        return formatter.print(parser.parseDateTime(it))
                    } catch (Exception e) {
                        return it //period label is not a date
                    }
                }
                return periodLabels
            }
        } catch (Exception ex) {

        }

        // Saving period labels is not possible for certain period counters.. they have to be resolved here
        try {
            SimpleDateFormat format = new SimpleDateFormat(DateFormatUtils.PARAMETER_DISPLAY_FORMAT)
            Class modelClass = ModellingItemFactory.getClassLoader().loadClass(run.model)
            Model simulationModel = (Model) modelClass.newInstance()
            IPeriodCounter periodCounter = simulationModel.createPeriodCounter(run.beginOfFirstPeriod)
            periodCounter.reset()
            run.periodCount.times {
                periodLabels << format.format(periodCounter.getCurrentPeriodStart().toDate())
                periodCounter.next()
            }
            return periodLabels
        } catch (Exception ex) {
        }

        // in case period labels are not found in the parametrization and not provided by the model
        run.periodCount.times {int i ->
            periodLabels << "P$i"
        }

        // the elements of the list need to be of type String not GString otherwise,
        // when used in ULCComboBox, an exception is thrown
        periodLabels.size().times { int i ->
            periodLabels[i] = new String(periodLabels[i])
        }

        return periodLabels
    }
}
