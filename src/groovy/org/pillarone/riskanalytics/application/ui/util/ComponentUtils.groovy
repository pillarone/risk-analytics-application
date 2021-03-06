package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.core.model.Model

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
            if (!tokens.get(0) != 'sub') {
                tokens.add(0, "sub")
            }
            name = StringUtils.uncapitalize(tokens.collect { StringUtils.capitalize(it) }.join(""))
        }
        return name
    }

    public static String removeModelFromPath(String path, Model model) {
        if (path != null && path.startsWith(model.name)) {
            return path.substring(model.name.length() + 1)
        }
        return path
    }

    public static List<SimpleTableTreeNode> intersection(List<List<SimpleTableTreeNode>> setAs) {
        List<SimpleTableTreeNode> tmp = []
        for (List<SimpleTableTreeNode> setA : setAs) {
            for (SimpleTableTreeNode x : setA) {
                boolean add = true;
                for (List<SimpleTableTreeNode> setB : setAs) {
                    if (!setB.contains(x) || setB.indexOf(x) != setA.indexOf(x)) {
                        add = false;
                    }
                }
                if (add && !tmp.contains(x)) {
                    tmp.add(x);
                } else {
                    return tmp;
                }
            }
        }
        return tmp;
    }


}
