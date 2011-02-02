package org.pillarone.riskanalytics.application.ui.comment.view;


import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import com.ulcjava.base.application.*

public class ErrorPane implements TabbedPaneChangeListener {

    private ULCBoxPane content;
    private ULCBoxPane container;
    private ParameterViewModel model;

    public ErrorPane(ParameterViewModel model) {
        this.model = model;
        content = new ULCBoxPane();
        container = new ULCBoxPane(1, 0);
        container.setBackground(Color.white);

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(container));
    }

    public void addError(ParameterValidationError error) {
        container.add(ULCBoxPane.BOX_EXPAND_TOP, createLabel(error));
    }

    public void addErrors(Collection<ParameterValidationError> errors) {
        if (errors != null && !errors.isEmpty()) {
            for (ParameterValidationError error: errors) {
                addError(error);
            }
        } else {
            ULCBoxPane around = UIUtils.spaceAround(new ULCLabel(UIUtils.getText(ErrorPane.class, "noError")), 2, 10, 0, 0)
            around.setBackground(Color.white)
            container.add(ULCBoxPane.BOX_LEFT_TOP, around);
        }

        container.add(ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createVerticalGlue());
    }

    public void clear() {
        container.removeAll();
    }

    void tabbedPaneChanged(CommentFilter filter) {
        clear()
        if (filter != null)
            addErrors(model?.validationErrors?.findAll {filter.accept(it)})
        else
            addErrors(model.validationErrors)
    }



    private ULCComponent createLabel(ParameterValidationError error) {
        ULCBoxPane pane = new ULCBoxPane(2, 1);
        pane.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder(model.findNodeForPath(error.getPath()).getDisplayPath());
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        pane.setBorder(border);

        ULCLabel label = new ULCLabel();
        label.setForeground(Color.red);
        label.setText(error.getLocalizedMessage(LocaleResources.getLocale()));
        label.setFont(label.getFont().deriveFont(Font.PLAIN));

        pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createHorizontalStrut(2));
        pane.add(ULCBoxPane.BOX_EXPAND_TOP, label);
        return pane;
    }

    public ULCBoxPane getContent() {
        return content;
    }

    public void setVisible(boolean visibility) {
        container.setVisible(visibility);
    }

}
