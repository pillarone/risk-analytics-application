package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.MarkItemAsUnsavedListener
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ModellingUIItem extends AbstractUIItem {
    private final ModellingItem item

    ModellingUIItem(ModellingItem item) {
        this.item = item
    }

    abstract NavigationTableTreeModel getNavigationTableTreeModel()

    boolean isLoaded() {
        return item.loaded
    }

    void load(boolean completeLoad) {
        item.load(completeLoad)
    }

    void unload() {
        item.unload()
    }

    abstract RiskAnalyticsMainModel getRiskAnalyticsMainModel()

    void close() {
        riskAnalyticsMainModel.closeItem(null, this)
    }

    boolean isUsedInSimulation() {
        return item.usedInSimulation
    }

    boolean deleteDependingResults() {
        true
    }

    String createTitle() {
        String title = item.nameAndVersion
        if (item.changed) {
            title += MarkItemAsUnsavedListener.UNSAVED_MARK
        }
        return title
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
        ModellingUIItem openedItem = riskAnalyticsMainModel.getAbstractUIItem(item)
        if (openedItem) {
            close()
        }
        ModellingItemFactory.remove(item)
        riskAnalyticsMainModel.fireModelChanged()
        if (item instanceof Simulation) {
            riskAnalyticsMainModel.fireRowDeleted(item)
        }
    }

    void rename(String newName) {
        SimulationRun.withTransaction { status ->
            if (!item.loaded) {
                item.load()
            }
            ItemNode itemNode = TableTreeBuilderUtils.findNodeForItem(navigationTableTreeModel.root as IMutableTableTreeNode, item) as ItemNode
            itemNode.userObject = newName
            renameAllChildren(itemNode, name)
            riskAnalyticsMainModel.fireModelChanged()
        }
    }

    private void renameAllChildren(ItemNode itemNode, String name) {
        if (itemNode.itemNodeUIItem instanceof SimulationResultUIItem) {
            return
        }
        itemNode.childCount.times {
            ItemNode childNode = itemNode.getChildAt(it) as ItemNode
            childNode.itemNodeUIItem.rename(name)
        }
    }

    void save() {
        ExceptionSafe.protect {
            item.save()
        }
        riskAnalyticsMainModel.fireModelChanged()
        riskAnalyticsMainModel.fireModelItemChanged()
    }


    ModellingItem addItem(ModellingUIItem modellingUIItem, String name) {
        modellingUIItem.item.daoClass.withTransaction { status ->
            if (!modellingUIItem.loaded) {
                modellingUIItem.load()
            }
            ModellingItem newItem = ModellingItemFactory.copyItem(modellingUIItem.item, name)
            newItem.id = null
            riskAnalyticsMainModel.fireModelChanged()
            newItem
        }
    }

    void importItem() {
        riskAnalyticsMainModel.fireModelChanged()
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


    VersionNumber getVersionNumber() {
        return item.versionNumber
    }

    Class getItemClass() {
        return item.class
    }

    @CompileStatic
    ModellingItem getItem() {
        return item
    }

    String getWindowTitle() {
        createTitle()
    }
}
