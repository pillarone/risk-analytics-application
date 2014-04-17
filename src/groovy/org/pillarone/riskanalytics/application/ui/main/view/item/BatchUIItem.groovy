package org.pillarone.riskanalytics.application.ui.main.view.item

import com.google.common.base.Preconditions
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.UlcUtilities
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class BatchUIItem extends ModellingUIItem {

    private static final String NEWBATCH = 'newbatch'

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

    BatchUIItem() {
        super(new Batch(NEWBATCH))
    }


    boolean isNewBatch() {
        item.name == NEWBATCH
    }

    String createTitle() {
        return newBatch ? UIUtils.getText(BatchUIItem.class, NEWBATCH) : item.name
    }

    ULCContainer createDetailView() {
        AbstractView view = BatchView.getView(this)
        view.init()
        return view.content
    }

    AbstractModellingModel getViewModel() {
        return null
    }

    void createNewBatch(ULCComponent parent, Batch newBatch) {
        if (validate(newBatch.name)) {
            newBatch.save()
        } else {
            new I18NAlert(UlcUtilities.getWindowAncestor(parent), "BatchNotValidName").show()
        }
    }

    protected boolean validate(String batchName) {
        return StringUtils.isNotEmpty(batchName) && StringUtils.isNotBlank(batchName) && BatchRun.findByName(batchName) == null
    }

    boolean remove() {
        if (batchRunService.deleteBatch(item)) {
            return true
        }
        return false
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
