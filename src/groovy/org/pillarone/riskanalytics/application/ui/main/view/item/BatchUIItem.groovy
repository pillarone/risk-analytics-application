package org.pillarone.riskanalytics.application.ui.main.view.item

import com.google.common.base.Preconditions
import com.ulcjava.base.application.ULCContainer
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class BatchUIItem extends ModellingUIItem {

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    BatchUIItem(Batch batch) {
        super(Preconditions.checkNotNull(batch))
    }

    ULCContainer createDetailView() {
        BatchView batchView = Holders.grailsApplication.mainContext.getBean('batchView', BatchView)
        //TODO this batchView has to be destroyed after usage!!
        batchView.batch = item
        batchView.content
    }

    AbstractModellingModel getViewModel() {
        null
    }

    boolean remove() {
        batchRunService.deleteBatch(item)
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof BatchUIItem)) {
            return false
        }
        return item && obj.item && item.name == obj.item.name
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        if (item) {
            hcb.append(item.name)
        }
        return hcb.toHashCode()
    }

    @Override
    String getWindowTitle() {
        return "Batches " + super.windowTitle
    }

    @Override
    String getName() {
        return item.name
    }

    @Override
    VersionNumber getVersionNumber() {
        return null
    }

    @Override
    Class getItemClass() {
        return Batch
    }

    @Override
    @CompileStatic
    Batch getItem() {
        super.getItem() as Batch
    }
}
