package org.pillarone.riskanalytics.application.search

import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem


class AllFieldsFilter implements ISearchFilter {

    String query = ""


    @Override
    boolean accept(ModellingItem item) {
        return StringUtils.containsIgnoreCase(item.name, query)
    }
}
