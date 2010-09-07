package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.comment.model.ItemListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewCommentView {
    ULCBoxPane content
    ULCTextArea commentTextArea
    ULCList tags
    ULCButton addButton
    ULCButton cancelButton
    final Dimension dimension = new Dimension(140, 20)
    int periodIndex
    String path

    private ParameterViewModel model;
    protected CommentAndErrorView commentAndErrorView
    ItemListModel<Tag> tagListModel

    public NewCommentView(CommentAndErrorView commentAndErrorView) {
        this.commentAndErrorView = commentAndErrorView
        this.model = commentAndErrorView.model
        List allTags = Tag.findAll()
        this.tagListModel = new ItemListModel<Tag>(allTags?.collect {it.name}.toArray(), allTags)
    }

    public NewCommentView(CommentAndErrorView commentAndErrorView, String path, int periodIndex) {
        this(commentAndErrorView)
        this.periodIndex = periodIndex
        this.path = path
        List allTags = Tag.findAll()
        this.tagListModel = new ItemListModel<Tag>(allTags?.collect {it.name}.toArray(), allTags)

        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        commentTextArea = new ULCTextArea(5, 65)
        commentTextArea.lineWrap = true
        commentTextArea.wrapStyleWord = true

        tags = new ULCList(tagListModel)
        tags.setVisibleRowCount(6);

        addButton = new ULCButton("Apply")
        addButton.setPreferredSize(dimension)

        cancelButton = new ULCButton("Cancel")
        cancelButton.setPreferredSize(dimension)
        content = new ULCBoxPane(3, 3)
        content.setPreferredSize(new Dimension(400, 160))
        String borderTitle = getDisplayPath() + (periodIndex == -1) ? " for all periods" : " P" + periodIndex
        final ULCTitledBorder border = BorderFactory.createTitledBorder(borderTitle);
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);
    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, UIUtils.spaceAround(new ULCScrollPane(commentTextArea), 5, 5, 0, 0))
        ULCScrollPane scrollList = new ULCScrollPane(tags)
        content.add(ULCBoxPane.BOX_LEFT_TOP, UIUtils.spaceAround(scrollList, 5, 0, 0, 0))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
        content.add(ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
        content.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
    }

    protected ULCBoxPane getButtonsPane() {
        ULCBoxPane pane = new ULCBoxPane(2, 1)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, addButton)
        pane.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancelButton)
        return pane
    }

    protected void attachListeners() {
        addButton.addActionListener([actionPerformed: {ActionEvent evt ->
            String text = commentTextArea.getText()
            if (text && text.length() > 0) {
                Comment comment = new Comment(path, periodIndex)
                comment.lastChange = new Date()
                comment.comment = commentTextArea.getText()
                tagListModel.getSelectedValues(tags.getSelectedIndices()).each {Tag tag ->
                    comment.addTag(tag)
                }
                model.addComment(comment)
                commentAndErrorView.closeTab()
            } else {
                //todo: alert text required
            }
        }] as IActionListener)

        cancelButton.addActionListener([actionPerformed: {ActionEvent evt ->
            commentAndErrorView.closeTab()
        }] as IActionListener)

    }

    String getDisplayPath() {
        return ""
    }
}
