package org.pillarone.riskanalytics.application.ui.comment.view;


import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font

import com.ulcjava.base.application.*

import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.application.ui.comment.action.MakeVisibleAction

public class ErrorPane implements TabbedPaneChangeListener {

    private ULCBoxPane content;
    private ULCBoxPane container;
    ULCButton makeVisibleButton
    MakeVisibleAction makeVisibleAction
    private AbstractCommentableItemModel model;
    private CommentAndErrorView commentAndErrorView

    public ErrorPane(AbstractCommentableItemModel model, CommentAndErrorView commentAndErrorView) {
        this.model = model;
        this.commentAndErrorView = commentAndErrorView
        content = new ULCBoxPane();
        container = new ULCBoxPane(1, 0);
        container.setBackground(Color.white);

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(container));
    }

    public void addError(ParameterValidation error) {
        container.add(ULCBoxPane.BOX_EXPAND_TOP, createLabel(error));
    }

    public void addErrors(Collection<ParameterValidation> errors) {
        if (errors != null && !errors.isEmpty()) {
            for (ParameterValidation error: errors) {
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



    private ULCComponent createLabel(ParameterValidation error) {
        ULCBoxPane pane = new ULCBoxPane(2, 1);
        pane.setBackground(Color.white);
        def node = model.findNodeForPath(error.getPath())
        final ULCTitledBorder border = BorderFactory.createTitledBorder(node?.getDisplayPath());
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        Color color = getColor(error.validationType)
        border.setTitleColor(color)
        pane.setBorder(border);

        ULCLabel label = new ULCLabel();
        label.setForeground(color);
        label.setText(error.getLocalizedMessage(LocaleResources.getLocale()));
        label.setFont(label.getFont().deriveFont(Font.PLAIN));

        makeVisibleAction = new MakeVisibleAction(model, node?.path)
        makeVisibleAction.addCommentListener(commentAndErrorView)
        makeVisibleButton = new ULCButton(makeVisibleAction)
        makeVisibleButton.setContentAreaFilled false
        makeVisibleButton.setBackground Color.white
        makeVisibleButton.setOpaque false

        pane.add(ULCBoxPane.BOX_LEFT_TOP, ULCFiller.createHorizontalStrut(2));
        ULCBoxPane textPane = new ULCBoxPane(3, 1)
        textPane.setBackground(Color.white);
        textPane.add(ULCBoxPane.BOX_LEFT_TOP, label)
        textPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        textPane.add(ULCBoxPane.BOX_RIGHT_TOP, makeVisibleButton)
        pane.add(ULCBoxPane.BOX_EXPAND_TOP, textPane);
        return pane;
    }

    public ULCBoxPane getContent() {
        return content;
    }

    public void setVisible(boolean visibility) {
        container.setVisible(visibility);
    }

    private Color getColor(ValidationType validationType) {
        switch (validationType) {
            case ValidationType.ERROR: return Color.red
            case ValidationType.WARNING: return Color.darkGray
            case ValidationType.HINT: return Color.blue
            default: return Color.black
        }
    }

}
