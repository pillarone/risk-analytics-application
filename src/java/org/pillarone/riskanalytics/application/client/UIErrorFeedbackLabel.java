package org.pillarone.riskanalytics.application.client;

import com.ulcjava.base.client.UILabel;
import com.ulcjava.base.client.datatype.DataTypeConversionException;
import com.ulcjava.base.client.datatype.UIDataType;
import com.ulcjava.base.client.tabletree.JTableTree;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;

public class UIErrorFeedbackLabel extends UILabel {

    @Override
    public Component getTableTreeCellRendererComponent(JTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node, int row, int column) {
        Component result = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node, row, column);
        visualizeErrorIfNeeded(value);
        return result;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getModel().isCellEditable(row, column)) {
            visualizeErrorIfNeeded(value);
        }
        return result;
    }

    private void visualizeErrorIfNeeded(Object value) {
        if (fDataType != null && isNotEmpty(value)) {
            try {
                if (value instanceof Double || value instanceof Integer) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setGroupingUsed(false);
                    fDataType.convertToObject(numberFormat.format(value), fValue);
                } else if (value instanceof Date) {
                    fDataType.convertToObject(fDataType.convertToString(value, false), fValue);
                } else {
                    fDataType.convertToObject(String.valueOf(value), fValue);
                }
                getBasicLabel().setBorder(null);
                getBasicLabel().setOpaque(true);
                getBasicLabel().setToolTipText(null);
            } catch (DataTypeConversionException e) {
                getBasicLabel().setText(value == null ? "" : String.valueOf(value));
                getBasicLabel().setBorder(BorderFactory.createLineBorder(Color.red, 2));
                getBasicLabel().setOpaque(true);
                String message = ((UIDataType) fDataType).getErrorManager().getLocalizedMessage(e.getErrorObject());
                getBasicLabel().setToolTipText(message);
            }

        } else if (fDataType != null && !isNotEmpty(value)) {
            getBasicLabel().setText("");
            getBasicLabel().setOpaque(true);
            getBasicLabel().setToolTipText("");

        }
    }

     private boolean isNotEmpty(Object obj){
        return (obj != null && !"".equals(obj)) ;
    }
}
