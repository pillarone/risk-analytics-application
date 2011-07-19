package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.main.view.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TableTreeBuilderUtils {

    public static ModelNode findModelNode(DefaultMutableTableTreeNode root, ModellingItem item) {
        return findModelNode(root, item.modelClass.name)
    }

    public static ModelNode findModelNode(DefaultMutableTableTreeNode root, AbstractUIItem abstractUIItem) {
        return findModelNode(root, abstractUIItem.item.modelClass.name)
    }

    public static ModelNode findModelNode(DefaultMutableTableTreeNode root, String modelClassName) {
        ModelNode modelNode = null
        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            def candidate = root.getChildAt(i)
            if (candidate.abstractUIItem.item.getClass().name.equals(modelClassName)) {
                modelNode = candidate
            }
        }
        return modelNode
    }

    public static BatchRootNode findBatchRootNode(DefaultMutableTableTreeNode root) {
        for (int i = 0; i < root.childCount; i++) {
            DefaultMutableTableTreeNode candidate = root.getChildAt(i)
            if (candidate instanceof BatchRootNode) {
                return candidate
            }
        }
        return null
    }


    public static ItemGroupNode findGroupNode(ModellingUIItem modellingUIItem, ModelNode modelNode) {
        return findGroupNode(modellingUIItem.item.class, modelNode)
    }

    public static ItemGroupNode findGroupNode(ModellingItem item, ModelNode modelNode) {
        return findGroupNode(item.class, modelNode)
    }

    public static ItemGroupNode findGroupNode(Class itemClass, ModelNode modelNode) {
        DefaultMutableTableTreeNode groupNode = null
        for (int i = 0; i < modelNode.childCount && groupNode == null; i++) {
            ITableTreeNode childNode = modelNode.getChildAt(i)
            if (childNode.itemClass == itemClass) {
                groupNode = childNode
            }
        }
        groupNode
    }

    public static ItemGroupNode findGroupNode(Parameterization item, ModelNode modelNode) {
        ItemGroupNode groupNode = null
        for (int i = 0; i < modelNode.childCount && groupNode == null; i++) {
            ItemGroupNode childNode = modelNode.getChildAt(i)
            if (childNode.itemClass == Parameterization) {
                groupNode = childNode
            }
        }
        return groupNode
    }

    public static ITableTreeNode findNodeForItem(ITableTreeNode node, Object item) {
        ITableTreeNode nodeForItem = null
        if (ItemComparator.isEqual(item, node)) {
            nodeForItem = node
        } else {
            for (int i = 0; i < node.childCount && nodeForItem == null; i++) {
                def childNode = node.getChildAt(i)
                nodeForItem = findNodeForItem(childNode, item)
            }
        }
        return nodeForItem
    }

    public static AbstractUIItem findUIItemForItem(ITableTreeNode root, Object item) {
        ModelNode modelNode1 = findModelNode(root, item)
        if (modelNode1) {
            ItemGroupNode itemGroupNode = findGroupNode(item, modelNode1)
            if (itemGroupNode) {
                ITableTreeNode node = findNodeForItem(itemGroupNode, item)
                if (node) return node.abstractUIItem
            }
        }
        return null
    }

}
