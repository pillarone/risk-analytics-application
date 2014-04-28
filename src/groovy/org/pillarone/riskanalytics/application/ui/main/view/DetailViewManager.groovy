package org.pillarone.riskanalytics.application.ui.main.view

import com.google.common.base.Preconditions
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class DetailViewManager {

    private final Map<AbstractUIItem, IDetailView> detailViewMap = [:]

    @PreDestroy
    void closeAll() {
        detailViewMap.values().each {
            it.close()
        }
        detailViewMap.clear()
    }

    IDetailView createDetailViewForItem(AbstractUIItem uiItem) {
        Preconditions.checkNotNull(uiItem)
        if (detailViewMap[uiItem]) {
            throw new IllegalStateException("there is already a detailview for item $uiItem. You must first close it.")
        }
        detailViewMap[uiItem] = uiItem.createDetailView()
        return detailViewMap[uiItem]
    }


    IDetailView getDetailViewForItem(AbstractUIItem uiItem) {
        Preconditions.checkNotNull(uiItem)
        return detailViewMap[uiItem]
    }

    void close(AbstractUIItem uiItem) {
        detailViewMap.remove(uiItem)?.close()
    }
}
