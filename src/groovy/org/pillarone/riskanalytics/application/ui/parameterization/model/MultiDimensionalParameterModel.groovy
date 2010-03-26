package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IComboBoxBasedMultiDimensionalParameter

class MultiDimensionalParameterModel implements IModelChangedListener {
    private ITableTreeModel model
    private MultiDimensionalParameterizationTableTreeNode node
    private int columnIndex
    AbstractMultiDimensionalParameter multiDimensionalParameter
    MultiDimensionalParameterTableModel tableModel
    TreePath selectedPath

    public MultiDimensionalParameterModel(ITableTreeModel model, MultiDimensionalParameterizationTableTreeNode node, int columnIndex) {
        this.model = model
        this.node = node
        this.columnIndex = columnIndex
        this.multiDimensionalParameter = node.getMultiDimensionalValue(columnIndex)
        tableModel = new MultiDimensionalParameterTableModel(multiDimensionalParameter)
        tableModel.addListener this
        ClientContext.setModelUpdateMode(tableModel, UlcEventConstants.SYNCHRONOUS_MODE)
        if (multiDimensionalParameter instanceof IComboBoxBasedMultiDimensionalParameter || multiDimensionalParameter instanceof ConstrainedMultiDimensionalParameter) {
            multiDimensionalParameter.validateValues()
            modelChanged()
        }
    }

    void save() {
        model.setValueAt(multiDimensionalParameter, node, columnIndex)
    }

    String getPathAsString() {
        node.displayPath
    }

    public void modelChanged() {
        model.setValueAt(multiDimensionalParameter, node, columnIndex)
    }


}