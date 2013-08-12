package org.pillarone.riskanalytics.application.search

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem


public interface ISearchFilter {

    boolean accept(ModellingItem item)

}