package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.BatchListener
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.BatchRun

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchUIItem extends AbstractUIItem {

    BatchRun batchRun
    RiskAnalyticsMainModel model
    AbstractTableTreeModel tableTreeModel

    public BatchUIItem(RiskAnalyticsMainModel model, BatchRun batchRun) {
        super(model, null)
        this.model = model
        this.batchRun = batchRun
    }

    public BatchUIItem(RiskAnalyticsMainModel model, AbstractTableTreeModel tableTreeModel, BatchRun batchRun) {
        this(model, batchRun)
        this.tableTreeModel = tableTreeModel
    }

    String createTitle() {
        return batchRun ? batchRun.name : "new batch"
    }

    ULCContainer createDetailView() {
        AbstractView view = BatchView.getView(this)
        view.init()
        return view.content
    }

    AbstractModellingModel getViewModel() {
        return null
    }

    Object getItem() {
        return batchRun
    }

    void createNewBatch(ULCComponent parent, BatchRun newBatch) {
        if (validate(newBatch.name)) {
            BatchRun.withTransaction {
                newBatch.save()
            }
            addBatchRun(BatchRun.findByName(newBatch.name))
        } else {
            new I18NAlert(UlcUtilities.getWindowAncestor(parent), "BatchNotValidName").show()
        }

    }

    protected boolean validate(String batchName) {
        return StringUtils.isNotEmpty(batchName) && StringUtils.isNotBlank(batchName) && BatchRun.findByName(batchName) == null
    }

    public void addBatchRun(BatchRun batchRun) {
        if (!batchRun) return
        this.batchRun = batchRun
        tableTreeModel.addNodeForItem(this)
        mainModel.viewModelsInUse.each {k, v ->
            if (v instanceof BatchListener)
                v.newBatchAdded(batchRun)
        }
        //todo fja
        //TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, oldName, batchRun.name)
    }

    public boolean remove() {
        if (batchRun.batchRunService.deleteBatchRun(batchRun)) {
            tableTreeModel.removeNodeForItem(this)
            mainModel.fireModelChanged()
            return true
        }
        return false
    }


}
