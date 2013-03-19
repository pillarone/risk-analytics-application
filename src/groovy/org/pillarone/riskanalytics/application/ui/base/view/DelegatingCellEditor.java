package org.pillarone.riskanalytics.application.ui.base.view;

import com.ulcjava.base.application.DefaultCellEditor;
import com.ulcjava.base.application.IEditorComponent;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.ULCTextField;
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor;

import java.util.HashMap;

public class DelegatingCellEditor extends DefaultCellEditor implements ITableTreeCellEditor {

    private HashMap<Class, ITableTreeCellEditor> editors;

    public DelegatingCellEditor(HashMap<Class, ITableTreeCellEditor> editors) {
        super(new ULCTextField());
        this.editors = editors;
    }

    public IEditorComponent getTableTreeCellEditorComponent(ULCTableTree ulcTableTree, Object value,
                                                            boolean selected, boolean expanded,
                                                            boolean leaf, Object node) {
        ITableTreeCellEditor editor = editors.get(node.getClass());

        if (editor != null) {
            return editor.getTableTreeCellEditorComponent(ulcTableTree, value, selected, expanded, leaf, node);
        } else {
            return super.getTableTreeCellEditorComponent(ulcTableTree, value, selected, expanded, leaf, node);
        }
    }
}
