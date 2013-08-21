package org.pillarone.riskanalytics.application.search

import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.Simulation


class AllFieldsFilter implements ISearchFilter {

    static final String OR_SEPARATOR = ' OR '
    String query = ""


    @Override
    boolean accept(ModellingItem item) {
        String[] searchStrings = query.split(OR_SEPARATOR)
        boolean accept = false
        searchStrings.each {
            accept |= StringUtils.containsIgnoreCase(item.nameAndVersion, it.trim())
        }
        return accept || item.creator?.username?.equalsIgnoreCase(query) ||
                internalAccept(item)

    }

    boolean internalAccept(ModellingItem item) {
        return false
    }

    boolean internalAccept(Simulation item) {
        return StringUtils.containsIgnoreCase(item.parameterization?.name, query) ||
                StringUtils.containsIgnoreCase(item.template?.name, query) ||
                item.tags*.name.any { StringUtils.containsIgnoreCase(it, query) }
    }

    protected boolean internalAccept(Parameterization item) {
        return item.tags*.name.any { StringUtils.containsIgnoreCase(it, query) } ||
                StringUtils.equals(item.dealId.toString(), query)
    }

    protected boolean internalAccept(Resource item) {
        return item.tags*.name.any { StringUtils.containsIgnoreCase(it, query) }
    }

}
