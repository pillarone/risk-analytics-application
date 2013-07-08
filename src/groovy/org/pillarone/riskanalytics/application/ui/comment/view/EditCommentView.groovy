package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCListSelectionModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.event.*
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EditCommentView extends NewCommentView {
    ULCButton saveButton
    Comment comment

    public EditCommentView(CommentAndErrorView commentAndErrorView, Comment comment) {
        super(commentAndErrorView);
        this.comment = comment
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        super.initComponents();
        saveButton = new ULCButton(UIUtils.getText(EditCommentView.class, "Save"))
        saveButton.name = "updateComment"
        saveButton.setPreferredSize(dimension)
        this.commentTextArea.setText(comment.getText())
        tags.setSelectedIndices(tagListModel.getSelectedIndices(comment?.getTags()?.collect { it.name }))
        comment?.files?.each { fileAdded(it) }
    }

    protected void attachListeners() {
        tags.addListSelectionListener([valueChanged: { ListSelectionEvent event ->
            ULCListSelectionModel selectionModel = (ULCListSelectionModel) event.getSource()
            comment.setTags(tagListModel.getSelectedValues(selectionModel.getSelectedIndices()))
            addPostTag(comment)

        }] as IListSelectionListener)
        commentTextArea.addValueChangedListener([valueChanged: { ValueChangedEvent event -> this.comment.setText(commentTextArea.getText()) }] as IValueChangedListener)
        saveButton.addActionListener([actionPerformed: { ActionEvent evt ->
            comment.clearFiles()
            addedFiles.each { CommentFile f ->
                comment.addFile(f)
            }
            comment.updated = true
            commentAndErrorView.model.commentChanged(null)
            saveComments(commentAndErrorView.model.item)
            commentAndErrorView.closeTab()
        }] as IActionListener)
        cancelButton.addActionListener([actionPerformed: { ActionEvent evt ->
            commentAndErrorView.closeTab()
        }] as IActionListener)


    }

    protected ULCBoxPane getButtonsPane() {
        ULCBoxPane pane = new ULCBoxPane(3, 1)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, addFileButton)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, saveButton)
        pane.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancelButton)
        return pane
    }

    String getDisplayPath() {
        return CommentAndErrorView.getDisplayPath(commentAndErrorView.model, comment.path)
    }
}
