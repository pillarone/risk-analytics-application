package org.pillarone.riskanalytics.application.ui.main.view.item
import com.google.common.base.Preconditions
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class BatchUIItem extends ModellingUIItem<BatchView> {

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean('batchRunService', BatchRunService)
    }

    BatchUIItem(Batch batch) {
        super(Preconditions.checkNotNull(batch))
    }

    BatchView createDetailView() {
        BatchView batchView = Holders.grailsApplication.mainContext.getBean('batchView', BatchView)
        batchView.batch = item
        batchView
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
        return "Batch " + super.windowTitle
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
