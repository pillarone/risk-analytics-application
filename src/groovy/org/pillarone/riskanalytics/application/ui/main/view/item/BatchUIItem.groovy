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
import org.pillarone.riskanalytics.application.ui.main.view.MarkItemAsUnsavedListener
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.application.ui.base.view.IModelItemChangeListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchUIItem extends AbstractUIItem {

    BatchRun batchRun
    List<IModellingItemChangeListener> itemChangeListeners = []

    public BatchUIItem(RiskAnalyticsMainModel mainModel, BatchRun batchRun) {
        super(mainModel, null)
        this.batchRun = batchRun
    }


    String createTitle() {
        return batchRun ? batchRun.name : UIUtils.getText(BatchUIItem.class, "newbatch")
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
        navigationTableTreeModel.addNodeForItem(this)
        mainModel.viewModelsInUse.each {k, v ->
            if (v instanceof BatchListener)
                v.newBatchAdded(batchRun)
        }
    }

    public boolean remove() {
        if (batchRun.batchRunService.deleteBatchRun(batchRun)) {
            navigationTableTreeModel.removeNodeForItem(this)
            mainModel.fireModelChanged()
            return true
        }
        return false
    }

    @Override
    public void addModellingItemChangeListener(IModellingItemChangeListener listener) {
        itemChangeListeners << listener
    }

    public void removeModellingItemChangeListener(IModellingItemChangeListener listener) {
        itemChangeListeners.remove(listener)
    }

    public void removeAllModellingItemChangeListener() {
        itemChangeListeners.clear()
    }

    public void notifyItemSaved() {
        itemChangeListeners.each {IModellingItemChangeListener listener ->
            listener.itemSaved(null)
        }
    }

    @Override
    boolean equals(Object obj) {
        return batchRun && obj.batchRun && batchRun.name == obj.batchRun.name
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        if (batchRun)
            hcb.append(batchRun.name)
        return hcb.toHashCode()
    }

    @Override
    String getWindowTitle() {
        return "Batches " + super.getWindowTitle()
    }

    @Override
    String getName() {
        return batchRun ?  batchRun.name : ""
    }
}
