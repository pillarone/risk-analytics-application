package org.pillarone.riskanalytics.application.search

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization


class StatusFilter extends AbstractMultiValueFilter {

    @Override
    boolean accept(ModellingItem item) {
        if (!valueList.empty) {
            return internalAccept(item)
        }

        return true
    }

    boolean internalAccept(ModellingItem item) {
        return false
    }

    boolean internalAccept(Parameterization item) {
        return valueList.contains(item.status.toString())
    }
}
