package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentSearchPane {
    private ULCBoxPane content;
    private ParameterViewModel model
    ULCToggleButton selectCommentButton
    ULCToggleButton selectValidationButton
    ULCButton searchButton
    ULCTextField searchText
//    CommentAndErrorView commentAndErrorView
    ULCComponent commentsView
    ULCComponent errorsView

    public CommentSearchPane(ULCComponent commentsView, ULCComponent errorsView, model) {
        this.model = model;
        this.commentsView = commentsView
        this.errorsView = errorsView
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        content = new ULCBoxPane(5, 1);
        content.name = "CommentSearchPane"
        content.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder("");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);
        selectCommentButton = new ULCToggleButton("Comments")
        selectCommentButton.setSelected(true)
        selectValidationButton = new ULCToggleButton("Validations")
        selectValidationButton.setSelected(true)
        searchButton = new ULCButton("Search")
        searchText = new ULCTextField()
        searchText.setPreferredSize(new Dimension(200, 20))
    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, selectValidationButton);
        content.add(ULCBoxPane.BOX_LEFT_TOP, selectCommentButton)
        content.add(ULCBoxPane.BOX_LEFT_TOP, searchText);
        content.add(ULCBoxPane.BOX_LEFT_TOP, searchButton);
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    protected void attachListeners() {
        selectCommentButton.addActionListener([actionPerformed: {ActionEvent event ->
            commentsView.setVisible(selectCommentButton.selected)
        }] as IActionListener)
        selectValidationButton.addActionListener([actionPerformed: {ActionEvent event ->
            errorsView.setVisible(selectValidationButton.selected)
        }] as IActionListener)
    }
}
