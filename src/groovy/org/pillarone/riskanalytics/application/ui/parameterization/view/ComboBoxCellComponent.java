package org.pillarone.riskanalytics.application.ui.parameterization.view;

import com.ulcjava.base.application.IEditorComponent;
import com.ulcjava.base.application.IRendererComponent;
import com.ulcjava.base.application.ULCComboBox;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor;
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer;
import org.pillarone.riskanalytics.application.ui.base.view.IMultiValueTableTreeNode;


public class ComboBoxCellComponent extends ULCComboBox implements ITableTreeCellRenderer, ITableTreeCellEditor {


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree ulcTableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        TableTreeComboBoxModel model = createModel(node);
        model.setEditMode(false);
        setModel(model);
        return this;
    }

    public IEditorComponent getTableTreeCellEditorComponent(ULCTableTree tableTree, Object value, boolean selected, boolean expanded, boolean leaf, Object node) {
        TableTreeComboBoxModel model = createModel(node);
        model.setEditMode(true);
        setModel(model);
        return this;
    }

    private TableTreeComboBoxModel createModel(Object node) {
        return new TableTreeComboBoxModel(((IMultiValueTableTreeNode) node).getValues());
    }
}
