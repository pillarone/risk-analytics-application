package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

import org.pillarone.riskanalytics.core.parameter.comment.Tag

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.ui.comment.action.EditCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.RemoveCommentAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentPane {
    private ULCBoxPane content;
    private ULCBoxPane container;
    ULCLabel label
    ULCLabel tags
    ULCButton editButton
    ULCButton deleteButton
    Comment comment
    String path
    int periodIndex
    EditCommentAction editCommentAction
    RemoveCommentAction removeCommentAction
    private ParameterViewModel model
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat('dd.MM.yyyy HH:mm')

    public CommentPane(ParameterViewModel model, Comment comment) {
        this.model = model
        this.comment = comment
        initComponents()
        layoutComponents()
    }


    protected void initComponents() {
        content = new ULCBoxPane(3, 2);
        content.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder(getTitle());
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);

        label = new ULCLabel();
        label.setText HTMLUtilities.convertToHtml(comment.getText())
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        tags = new ULCLabel()
        tags.setText HTMLUtilities.convertToHtml(getTagsValue())
        editCommentAction = new EditCommentAction(comment)
        editButton = new ULCButton(editCommentAction)
        removeCommentAction = new RemoveCommentAction(model, comment)
        deleteButton = new ULCButton(removeCommentAction)

    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, tags);
        ULCBoxPane buttons = new ULCBoxPane(2, 1)
        buttons.add(editButton)
        buttons.add(deleteButton)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
        content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
    }


    void addCommentListener(CommentListener listener) {
        editCommentAction.addCommentListener listener
    }

    String getTagsValue() {
        int size = comment.getTags().size()
        StringBuilder sb = new StringBuilder("Tags: ")
        comment.getTags().eachWithIndex {Tag tag, int index ->
            sb.append(tag.getName())
            if (index < size - 1)
                sb.append(", ")
        }
        return sb.toString()
    }

    String getTitle() {
        String username = comment.user ? comment.user.userRealName : ""
        StringBuilder sb = new StringBuilder(CommentAndErrorView.getDisplayPath(model, comment.getPath()))
        sb.append((comment.getPeriod() != -1) ? " P" + comment.getPeriod() : " for all periods")
        sb.append(" " + username)
        sb.append(" " + simpleDateFormat.format(comment.lastChange))
        return sb.toString()
    }

}
