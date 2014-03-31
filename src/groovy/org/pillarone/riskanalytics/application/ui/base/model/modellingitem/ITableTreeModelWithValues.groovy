package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode

interface ITableTreeModelWithValues {
    void putValues(ItemNode node)

    void nodeStructureChanged(TreePath path)

    void nodeChanged(TreePath path)

    void nodesWereInserted(TreePath parentPath, int[] childIndices)

}
