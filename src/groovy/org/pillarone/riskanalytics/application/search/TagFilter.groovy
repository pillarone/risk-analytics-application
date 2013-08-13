package org.pillarone.riskanalytics.application.search

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class TagFilter extends AbstractMultiValueFilter {


    @Override
    boolean accept(ModellingItem item) {
        if (!valueList.empty) {
            return internalAccept(item)
        }

        return true
    }

    protected boolean internalAccept(ModellingItem item) {
        return false
    }

    protected boolean internalAccept(Parameterization item) {
        return item.tags*.name.any { valueList.contains(it) }
    }

    protected boolean internalAccept(Simulation item) {
        return item.tags*.name.any { valueList.contains(it) }
    }

    protected boolean internalAccept(Resource item) {
        return item.tags*.name.any { valueList.contains(it) }
    }
}
