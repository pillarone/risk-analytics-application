package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TabIdentifier {
    TreePath path
    Integer columnIndex

    public boolean equals(Object obj) {
        if (obj instanceof TabIdentifier) {
            return obj.path.equals(path) && obj.columnIndex.equals(columnIndex)
        } else {
            return false
        }
    }

    public int hashCode() {
        return path.hashCode() * columnIndex;
    }


}
