package org.pillarone.riskanalytics.application.ui.main.view

import com.google.common.base.Preconditions
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class DetailViewManager {

    AbstractUIItem currentUIItem

    private final Map<AbstractUIItem, IDetailView> detailViewMap = [:]

    @PostConstruct
    void initialize() {
    }

    @PreDestroy
    void closeAll() {
        detailViewMap.values().each {
            it.close()
        }
        detailViewMap.clear()
    }

    IDetailView createDetailViewForItem(AbstractUIItem uiItem) {
        //TODO we have to make a copy of the item. Otherwise it will conflict with the automated update of the navigation refactoring
        if (!uiItem.loaded) {
            uiItem.load()
        }
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

    IDetailView getOpenDetailView() {
        if (currentUIItem) {
            return detailViewMap[currentUIItem]
        }
        return null
    }


    void close(AbstractUIItem uiItem) {
        detailViewMap.remove(uiItem)?.close()
        //TODO each detailView should add and remove listeners itself
        if (uiItem instanceof ModellingUIItem) {
            uiItem.removeAllModellingItemChangeListener()
        }
    }

    void saveAllOpenItems() {
        modellingUiItems.each { it.save() }
    }

    private Set<ModellingUIItem> getModellingUiItems() {
        uiItems.findAll { it instanceof ModellingUIItem }
    }

    private Set<AbstractUIItem> getUiItems() {
        detailViewMap.keySet()
    }

    boolean isItemOpen(AbstractUIItem item) {
        uiItems.contains(item)
    }
}
