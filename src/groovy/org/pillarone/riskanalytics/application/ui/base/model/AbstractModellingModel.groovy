package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure

abstract class AbstractModellingModel {

    int periodCount
    Model model
    String structureFileName
    String modellingFileName
    ITableTreeModel treeModel
    def builder
    List nodeNames
    NodeNameFilter filter
    //TODO (msp): changed to object because of compare views
    Object item
    ModelStructure structure

    public AbstractModellingModel(Model model, Object item, ModelStructure modelStructure) {
        this.model = model
        this.item = item
        this.structure = modelStructure
        filter = new NodeNameFilter(null)
        ITableTreeModel tree = buildTree()
        if (tree) {
            treeModel = new FilteringTableTreeModel(tree, filter)
            changeUpdateMode(treeModel)
            nodeNames = extractNodeNames(treeModel)
        }

    }

    protected changeUpdateMode(def tableTreeModel) {
        //for tests without ULCApplication this will throw NullPointException, so you have to overwrite this method
        ClientContext.setModelUpdateMode(tableTreeModel, UlcEventConstants.SYNCHRONOUS_MODE)
    }

    protected abstract ITableTreeModel buildTree()

    protected List extractNodeNames(ITableTreeModel tableTreeModel) {
        Set names = Collections.EMPTY_SET
        if (tableTreeModel) {
            names = new TreeSet()
            collectChildNames(tableTreeModel.root, names)
        }
        return names as List
    }

    protected def collectChildNames(ITableTreeNode node, Set names) {
        node.childCount.times {
            collectChildNames(node.getChildAt(it), names)
        }

    }

    protected def collectChildNames(ComponentTableTreeNode node, Set names) {
        names << node.displayName
        node.childCount.times {
            collectChildNames(node.getChildAt(it), names)
        }
    }

    protected def collectChildNames(ResultStructureTableTreeNode node, Set names) {
        names << node.displayName
        node.childCount.times {
            collectChildNames(node.getChildAt(it), names)
        }
    }


    void updateNodeNameFilter(String nodeName) {
        filter.nodeName = nodeName
        if (treeModel instanceof FilteringTableTreeModel) {
            treeModel.filter = filter
        }
    }

    String getPeriodLabel(int periodIndex) {
        return "Q$periodIndex".toString()
    }

    public void saveItem() {

    }

    IActionListener getSaveAction() {
        return null
    }

}