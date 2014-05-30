package org.pillarone.riskanalytics.application.ui.main.view.item

import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.springframework.transaction.TransactionStatus

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIItemUtils {

    private static final Log LOG = LogFactory.getLog(UIItemUtils)

    public static SimulationQueueService getSimulationQueueService() {
        Holders.grailsApplication.mainContext.getBean('simulationQueueService', SimulationQueueService)
    }

    static boolean deleteDependingResults(ModellingItem item) {
        if (isUsedInRunningSimulation(item)) {
            return false
        }
        try {
            SimulationRun.withTransaction { TransactionStatus status ->
                List<Simulation> simulations = item.simulations.collect {
                    ModellingItemFactory.getOrCreate(it)
                }
                //check if at least one simulation is running
                List<Simulation> runsToBeRemoved = []
                for (Simulation simulationRun : simulations) {
                    if (!simulationRun.end) {
                        simulationRun.delete()
                        runsToBeRemoved << simulationRun
                    }
                }
                simulations.removeAll(runsToBeRemoved)
                for (Simulation simulation : simulations) {
                    simulation.delete()
                }
                Tag postLocking = Tag.findByNameAndTagType(NewCommentView.POST_LOCKING, EnumTagType.COMMENT)
                deleteCommentTag(item, postLocking)

            }
        } catch (Exception ex) {
            LOG.error("Error deleting depending results: ${ex.message}", ex)
            return false
        }
        return true
    }

    static boolean isUsedInRunningSimulation(ModellingItem item) {
        //PMO-2797 if null endtime sims found, verify they are sitting in sim queue
        //
        List<Simulation> itemSims = item.simulations
        if( itemSims.isEmpty() ){
            return false
        }
        List<Simulation> simsWithoutEndTime = itemSims.findAll { !it.end }
        if( simsWithoutEndTime.isEmpty() ){
            return false
        }
        if( !simsWithoutEndTime.isEmpty() ){
            List<Simulation> queuedSims = getSimulationQueueService().getQueueEntriesIncludingCurrentTask()*.simulationTask*.simulation
            for( Simulation queuedSim : queuedSims ){
                if( simsWithoutEndTime.any { it.equals( queuedSim )} ){
                    LOG.info( "Item $queuedSim is currently queued for simulation" );
                    return true
                }
            }
        }
        return false
    }

    static void deleteCommentTag(Parameterization parameterization, Tag tag) {
        parameterization.comments.each { Comment comment ->
            if (comment.tags.contains(tag)) {
                comment.removeTag(tag)
                parameterization.changed = true
            }
        }
        if (parameterization.changed) parameterization.save()
    }

    static void deleteCommentTag(ModellingItem modellingItem, Tag tag) {

    }

    static List<ModellingItem> getSelectedModellingItemsForReporting(List<ItemNode> selectedUIItems) {
        Collection<IReportableNode> reportingItems = (Collection<IReportableNode>) selectedUIItems.findAll { ItemNode uiItem -> uiItem instanceof IReportableNode }
        List<ModellingItem> modellingItems = reportingItems*.modellingItemsForReport().flatten()
        return modellingItems
    }

}
