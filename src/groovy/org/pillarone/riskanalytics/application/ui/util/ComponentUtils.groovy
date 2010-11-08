package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ComponentUtils {

    static boolean isDynamicComposedSubComponentNode(def node) {
        if (node instanceof ComponentTableTreeNode)
            return node.parent instanceof ComponentTableTreeNode && node.parent.component instanceof DynamicComposedComponent && node.parent.component.isDynamicSubComponent(node.component)
        return false
    }

    static boolean isDynamicComposedComponent(def node) {
        return (node instanceof ComponentTableTreeNode) && (node.component instanceof DynamicComposedComponent)
    }

    static String getSubComponentName(String name) {
        if (name.length() != 0) {
            List tokens = name.split(" ") as List
            if (!tokens.get(0).startsWith('sub')) {
                tokens[0] = 'sub' + tokens[0]
            }
            name = tokens.join("_")
        }
        return name
    }
}
