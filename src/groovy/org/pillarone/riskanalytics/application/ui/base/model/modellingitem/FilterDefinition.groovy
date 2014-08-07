package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import org.pillarone.riskanalytics.core.search.*
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class FilterDefinition {

    AllFieldsFilter allFieldsFilter = new AllFieldsFilter()
    TagFilter tagFilter = new TagFilter()
    StatusFilter statusFilter = new StatusFilter()
    OwnerFilter ownerFilter = new OwnerFilter()
    ExcludeClassesFilter excludeSimulationProfileFilter = new ExcludeClassesFilter([SimulationProfile])

    List<ISearchFilter> toQuery() {
        List<ISearchFilter> filters = []

        filters << allFieldsFilter
        filters << excludeSimulationProfileFilter

        if (!tagFilter.values.empty) {
            filters << tagFilter
        }
        if (!statusFilter.values.empty) {
            filters << statusFilter
        }
        if (ownerFilter.active) {
            filters << ownerFilter
        }
        return filters
    }

}
