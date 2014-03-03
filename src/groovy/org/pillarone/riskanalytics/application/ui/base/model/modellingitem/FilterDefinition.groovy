package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import org.pillarone.riskanalytics.core.search.*

class FilterDefinition {

    AllFieldsFilter allFieldsFilter = new AllFieldsFilter()
    TagFilter tagFilter = new TagFilter()
    StatusFilter statusFilter = new StatusFilter()
    OwnerFilter ownerFilter = new OwnerFilter()


    List<ISearchFilter> toQuery() {
        List<ISearchFilter> filters = []
        filters << allFieldsFilter
        filters << tagFilter
        filters << statusFilter
        filters << ownerFilter
        return filters
    }

}
