package org.pillarone.riskanalytics.application.ui.parameterization.view;

import com.ulcjava.base.application.DefaultComboBoxModel;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

public class TableTreeComboBoxModel extends DefaultComboBoxModel {

    private boolean editMode;

    public TableTreeComboBoxModel(List items) {
        super(items);
    }

    public TableTreeComboBoxModel() {
        super();
    }

    public boolean equals(Object obj) {
        if (obj instanceof TableTreeComboBoxModel) {
            TableTreeComboBoxModel model = (TableTreeComboBoxModel) obj;
            if (model.getSize() != getSize())
                return false;
            for (int i = 0; i < getSize(); i++) {
                if (!getElementAt(i).equals(model.getElementAt(i))) return false;
            }
            if (editMode != model.editMode) return false;
        } else {
            return false;
        }

        return true;
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        for (int i = 0; i < getSize(); i++) {
            builder.append(getElementAt(i));
        }
        builder.append(editMode);
        return builder.toHashCode();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
