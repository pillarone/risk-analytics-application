package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.application.ui.comment.action.EditCommentAction
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentPane {
    private ULCBoxPane content;
    private ULCBoxPane container;
    ULCLabel label
    ULCButton editButton
    ULCButton deleteButton
    Comment comment
    String path
    int periodIndex
    EditCommentAction editCommentAction

    public CommentPane(Comment comment) {
        this.comment = comment
        initComponents()
        layoutComponents()
    }


    protected void initComponents() {
        content = new ULCBoxPane(3, 2);
        content.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder(comment.getPath());
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);

        label = new ULCLabel();
        label.setForeground(Color.red);
        label.setText HTMLUtilities.convertToHtml(comment.getText())
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        editCommentAction = new EditCommentAction(comment)
        editButton = new ULCButton(editCommentAction)
        deleteButton = new ULCButton("delete")

    }

    protected void layoutComponents() {
        content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        content.add(ULCBoxPane.BOX_RIGHT_TOP, editButton)
        content.add(ULCBoxPane.BOX_RIGHT_TOP, deleteButton)
    }


    void addCommentListener(CommentListener listener) {
        editCommentAction.addCommentListener listener
    }

}
