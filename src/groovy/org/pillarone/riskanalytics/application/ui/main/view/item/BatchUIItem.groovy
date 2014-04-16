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

    public static final String NEWBATCH = "newbatch"

    BatchUIItem(RiskAnalyticsMainModel mainModel, Batch batch) {
        super(mainModel, null, Preconditions.checkNotNull(batch))
    }

    String createTitle() {
        return (item.name == NEWBATCH) ? UIUtils.getText(BatchUIItem.class, NEWBATCH) : item.name
    }

    ULCContainer createDetailView() {
        AbstractView view = BatchView.getView(this)
        view.init()
        return view.content
    }

    AbstractModellingModel getViewModel() {
        return null
    }

    void createNewBatch(ULCComponent parent, BatchRun newBatch) {
        if (validate(newBatch.name)) {
            BatchRun.withTransaction {
                newBatch.save()
            }
        } else {
            new I18NAlert(UlcUtilities.getWindowAncestor(parent), "BatchNotValidName").show()
        }

    }

    protected boolean validate(String batchName) {
        return StringUtils.isNotEmpty(batchName) && StringUtils.isNotBlank(batchName) && BatchRun.findByName(batchName) == null
    }

    boolean remove() {
        if (batchRunService.deleteBatchRun(item)) {
            return true
        }
        return false
    }

    private BatchRunService getBatchRunService() {
        Holders.grailsApplication.mainContext.getBean(BatchRunService)
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
