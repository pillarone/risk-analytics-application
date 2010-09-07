package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.application.event.*

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
        saveButton = new ULCButton("Save")
        saveButton.setPreferredSize(dimension)
        this.commentTextArea.setText(comment.getText())
        tags.setSelectedIndices(tagListModel.getSelectedIndices(comment?.getTags()?.collect {it.name}))
    }

    protected void attachListeners() {
        tags.addListSelectionListener([valueChanged: {ListSelectionEvent event ->
            ULCListSelectionModel selectionModel = (ULCListSelectionModel) event.getSource()
            comment.setTags(tagListModel.getSelectedValues(selectionModel.getSelectedIndices()))

        }] as IListSelectionListener)
        commentTextArea.addValueChangedListener([valueChanged: {ValueChangedEvent event -> this.comment.setText(commentTextArea.getText())}] as IValueChangedListener)
        saveButton.addActionListener([actionPerformed: {ActionEvent evt ->
            commentAndErrorView.model.commentChanged()
            commentAndErrorView.closeTab()
        }] as IActionListener)
        cancelButton.addActionListener([actionPerformed: {ActionEvent evt ->
            commentAndErrorView.closeTab()
        }] as IActionListener)


    }

    protected ULCBoxPane getButtonsPane() {
        ULCBoxPane pane = new ULCBoxPane(2, 1)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, saveButton)
        pane.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancelButton)
        return pane
    }

    String getDisplayPath() {
        return CommentAndErrorView.getDisplayPath(commentAndErrorView.model, comment.path)
    }

}
