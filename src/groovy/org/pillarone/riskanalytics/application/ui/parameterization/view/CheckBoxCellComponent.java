package org.pillarone.riskanalytics.application.ui.parameterization.view;

import com.ulcjava.base.application.*;
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor;
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer;

public class CheckBoxCellComponent extends ULCCheckBox implements ITableTreeCellRenderer, ITableTreeCellEditor {

    public CheckBoxCellComponent() {
        setHorizontalAlignment(ULCCheckBox.RIGHT);
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree ulcTableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        selected = (Boolean) value;
        return this;
    }

    public IEditorComponent getTableTreeCellEditorComponent(ULCTableTree tableTree, Object value, boolean selected, boolean expanded, boolean leaf, Object node) {
        selected = (Boolean) value;
        return this;
    }
}