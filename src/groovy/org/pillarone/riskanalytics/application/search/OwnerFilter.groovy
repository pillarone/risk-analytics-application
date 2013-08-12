package org.pillarone.riskanalytics.application.search

import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.UserManagement


class OwnerFilter implements ISearchFilter {

    boolean active = false

    @Override
    boolean accept(ModellingItem item) {
        return active ? item.creator.id == UserManagement.currentUser.id : true
    }
}
