package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNode
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.main.view.MarkItemAsUnsavedListener
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ModellingUIItem extends ItemNodeUIItem {
    private final ModellingItem item

    ModellingUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, ModellingItem item) {
        super(mainModel, simulationModel)
        this.item = item
    }

    @Override
    boolean isLoaded() {
        return item.loaded
    }

    @Override
    void load(boolean completeLoad) {
        item.load(completeLoad)
    }

    @Override
    void unload() {
        item.unload()
    }

    boolean isUsedInSimulation() {
        return item.usedInSimulation
    }

    @Override
    String createTitle() {
        String title = "$item.name v${item.versionNumber.toString()}".toString()
        if (item.changed)
            title += MarkItemAsUnsavedListener.UNSAVED_MARK
        return title
    }

    boolean deleteDependingResults(Model model) {
        return UIItemUtils.deleteDependingResults(mainModel, model, this)
    }

    ModellingUIItem createNewVersion(Model selectedModel, boolean openNewVersion = true) {
        ModellingItem modellingItem = null
        item.daoClass.withTransaction { status ->
            if (!item.loaded) {
                item.load()
            }
            modellingItem = ModellingItemFactory.incrementVersion(item)
        }
        mainModel.fireModelChanged()
        AbstractUIItem modellingUIItem = UIItemFactory.createItem(modellingItem, selectedModel, mainModel)
        if (openNewVersion) {
            mainModel.openItem(selectedModel, modellingUIItem)
        }
        return modellingUIItem
    }

    @Override
    boolean remove() {
        if (ModellingItemFactory.delete(item)) {
            closeItem()
            return true
        }
        return false
    }

    void closeItem() {
        ModellingUIItem openedItem = mainModel.getAbstractUIItem(item)
        if (openedItem)
            mainModel.closeItem(model, openedItem)
        ModellingItemFactory.remove(item)
        mainModel.fireModelChanged()
        if (item instanceof Simulation) mainModel.fireRowDeleted(item)
    }

    @Override
    void rename(String newName) {
        item.daoClass.withTransaction { status ->
            if (!item.loaded) {
                item.load()
            }
            ModellingItemNode itemNode = TableTreeBuilderUtils.findNodeForItem(navigationTableTreeModel.root as IMutableTableTreeNode, item) as ModellingItemNode
            itemNode.userObject = newName

            renameAllChildren(itemNode, name)
            mainModel.fireModelChanged()
        }
    }

    private void renameAllChildren(ModellingItemNode itemNode, String name) {
        if (itemNode.itemNodeUIItem instanceof SimulationResultUIItem) {
            return
        }
        itemNode.childCount.times {
            ItemNode childNode = itemNode.getChildAt(it) as ItemNode
            childNode.itemNodeUIItem.rename(name)
        }
    }

    @Override
    void save() {
        ExceptionSafe.protect {
            item.save()
        }
        mainModel.fireModelChanged()
        mainModel.fireModelItemChanged()
    }


    void addItem(ModellingUIItem modellingUIItem, String name) {
        modellingUIItem.item.daoClass.withTransaction { status ->
            if (!modellingUIItem.loaded)
                modellingUIItem.load()
            ModellingItem newItem = ModellingItemFactory.copyItem(modellingUIItem.item, name)
            newItem.id = null
            mainModel.fireModelChanged()
            modellingUIItem.model
            if (!(newItem instanceof Resource)) { //re-create model (PMO-1961) - do nothing if it's a resource
                Model modelInstance = newItem?.modelClass?.newInstance() as Model
                modelInstance?.init()
            }
        }
    }

    void importItem() {
        mainModel.fireModelChanged()
    }

    void removeAllModellingItemChangeListener() {
        item.removeAllModellingItemChangeListener()
    }

    void addModellingItemChangeListener(IModellingItemChangeListener listener) {
        item.addModellingItemChangeListener(listener)
    }

    @Override
    boolean isChanged() {
        return item.changed
    }

    String getName() {
        return item.name
    }

    @Override
    String getNameAndVersion() {
        return item.nameAndVersion
    }


    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof ModellingUIItem)) {
            return false
        }
        return item.equals(obj.item)
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(item.modelClass.toString())
        hcb.append(item.modelClass.name)
        return hcb.toHashCode()
    }

    @Override
    String toString() {
        item?.name
    }

    @Override
    Model getModel() {
        if (!super.model) {
            model = item.modelClass.newInstance() as Model
            model.init()
        }
        return super.model
    }

    @Override
    VersionNumber getVersionNumber() {
        return item.versionNumber
    }

    @Override
    Class getItemClass() {
        return item.class
    }

    @Override
    @CompileStatic
    ModellingItem getItem() {
        return item
    }
}
