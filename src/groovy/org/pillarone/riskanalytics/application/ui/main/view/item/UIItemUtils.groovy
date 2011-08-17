package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemUtils {

    static Log LOG = LogFactory.getLog(UIItemUtils)

    public static boolean deleteDependingResults(RiskAnalyticsMainModel mainModel, Model model, ModellingUIItem modellingUIItem) {
        return deleteDependingResults(mainModel, model, modellingUIItem.item)
    }

    public static boolean deleteDependingResults(RiskAnalyticsMainModel mainModel, Model model, ModellingItem item) {
        boolean usedInRunningSimulation = false
        try {
            SimulationRun.withTransaction {TransactionStatus status ->
                List<SimulationRun> simulationRuns = item.getSimulations();
                //check if at least one simulation is running
                List<SimulationRun> runsToBeRemoved = []
                for (SimulationRun simulationRun: simulationRuns) {
                    if (!simulationRun.endTime) {
                        BatchRunSimulationRun batchRunSimulationRun = BatchRunSimulationRun.findBySimulationRun(simulationRun)
                        if (batchRunSimulationRun) {
                            batchRunSimulationRun.delete()
                            simulationRun.delete()
                            runsToBeRemoved << simulationRun
                        } else {
                            usedInRunningSimulation = true
                        }
                    }
                }
                if (!usedInRunningSimulation) {
                    simulationRuns.removeAll(runsToBeRemoved)
                    for (SimulationRun simulationRun: simulationRuns) {
                        Simulation simulation = ModellingItemFactory.getSimulation(simulationRun)
                        SimulationUIItem simulationUIItem = new SimulationUIItem(mainModel, model, simulation)
                        simulationUIItem.remove()
                    }
                    Tag postLocking = Tag.findByNameAndTagType(NewCommentView.POST_LOCKING, EnumTagType.COMMENT)
                    deleteCommentTag(item, postLocking)
                }

            }
        } catch (Exception ex) {
            LOG.error "$ex"
            return false
        }
        return !usedInRunningSimulation
    }

    public static void deleteCommentTag(Parameterization parameterization, Tag tag) {
        parameterization.comments.each { Comment comment ->
            if (comment.tags.contains(tag)) {
                comment.removeTag(tag)
                parameterization.setChanged(true)
            }
        }
        if (parameterization.changed) parameterization.save()
    }

    public static void deleteCommentTag(ModellingItem modellingItem, Tag tag) {

    }


}
