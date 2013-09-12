package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.INavigationTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

@CompileStatic
class PopupMenuRegistry {
    private static Map<Object, List<ULCTableTreeMenuItemCreator>> popupMenuMap = [:]

    static void register(Class<? extends INavigationTreeNode> nodeClass, ULCTableTreeMenuItemCreator creator) {
        if (!popupMenuMap.containsKey(nodeClass)) {
            popupMenuMap.put(nodeClass, [])
        }
        popupMenuMap.get(nodeClass).add(creator)
    }

    static void register(Class<ItemGroupNode> nodeClass, Class<? extends ModellingItem> itemClass, ULCTableTreeMenuItemCreator creator) {
        String key = key(nodeClass, itemClass)
        if (!popupMenuMap.containsKey(key)) {
            popupMenuMap.put(key, [])
        }
        popupMenuMap.get(key).add(creator)
    }

    static List<ULCTableTreeMenuItemCreator> get(Class<? extends INavigationTreeNode> nodeClass) {
        popupMenuMap.get(nodeClass)
    }

    static List<ULCTableTreeMenuItemCreator> get(ItemGroupNode groupNode) {
        popupMenuMap.get(key(groupNode.class, groupNode.itemClass))
    }

    private static String key(Class groupNodeClass, Class itemClass) {
        "${groupNodeClass}_${itemClass}"
    }
}

interface ULCTableTreeMenuItemCreator {
    /**
     * Create a ULCMenuItem that holds an IAction instance ideally a ResourceBasedAction instance.
     * @param tree
     * @return
     */
    ULCMenuItem createComponent(ULCTableTree tree)
}