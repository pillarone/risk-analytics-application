package org.pillarone.riskanalytics.application.ui.parameterization.view;

import com.ulcjava.base.application.DefaultCellEditor;
import com.ulcjava.base.application.IEditorComponent;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.ULCTextField;
import com.ulcjava.base.application.datatype.IDataType;


public class BasicCellEditor extends DefaultCellEditor {

    private IDataType dataType;

    public BasicCellEditor(IDataType dataType) {
        super(new ULCTextField());
        this.dataType = dataType;
    }

    @Override
    public IEditorComponent getTableTreeCellEditorComponent(ULCTableTree tableTree, Object value, boolean selected, boolean expanded, boolean leaf, Object node) {

        ULCTextField editor = (ULCTextField) super.getTableTreeCellEditorComponent(tableTree, value, selected, expanded, leaf, node);
        editor.setDataType(dataType);
        return editor;
    }
}
