package org.pillarone.riskanalytics.application.reports;

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem;

import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */

/**
 * This interface marks a node as being valid to report against. It should also enforce the provision of reporting data
 */
public interface IReportableNode {

    /**
     * This is the list of model classes this node may be interested in reporting against.
     * @return the list of model classes this node may provide reporting data for
     */
    List<Class> modelsToReportOn();

    /**
     * The list of concrete modelling items the node will provide for the report
     * @return the list of modelling items this node wants to be in a report
     */
    List<ModellingItem> modellingItemsForReport();

}
