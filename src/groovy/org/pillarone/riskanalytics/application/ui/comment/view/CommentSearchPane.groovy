package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Insets
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.action.TextFieldFocusListener
import org.pillarone.riskanalytics.application.ui.comment.model.CommentSearchBean
import org.pillarone.riskanalytics.application.ui.comment.model.MapComboBoxModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.reports.comment.action.CommentReportAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentSearchPane {
    ULCBoxPane content;
    ULCToolBar toolBar
    private AbstractCommentableItemModel model
    ULCToggleButton selectCommentButton
    ULCToggleButton selectValidationButton
    ULCButton clearButton
    ULCButton exportToPdf
    ULCTextField searchText
    ULCLabel orderByLabel
    ULCComboBox orderByComboBox
    ULCComboBox orderComboBox
    ShowCommentsView commentsView
    ErrorPane errorsView
    ShowCommentsView resultView
    CommentSearchBean commentSearchBean
    DefaultComboBoxModel orderByComboBoxModel
    DefaultComboBoxModel orderComboBoxModel
    CommentReportAction reportAction

    public CommentSearchPane(ShowCommentsView commentsView, ErrorPane errorsView, ShowCommentsView resultView, model) {
        this.model = model;
        this.commentsView = commentsView
        this.errorsView = errorsView
        this.resultView = resultView
        this.resultView.setVisible false
        this.commentSearchBean = CommentSearchBean.getInstance(this.model.item)
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        content = new ULCBoxPane(2, 1);
        content.name = "CommentSearchPane"
        toolBar = new ULCToolBar("commentToolBar", ULCToolBar.HORIZONTAL);
        toolBar.margin = new Insets(2, 5, 2, 5)
        toolBar.floatable = false
        selectCommentButton = new ULCToggleButton(UIUtils.getText(this.class, "comments", null))
        selectCommentButton.setSelected(true)
        selectValidationButton = new ULCToggleButton(UIUtils.getText(this.class, "validations"))
        selectValidationButton.setSelected(true)
        clearButton = new ULCButton(UIUtils.getIcon("delete-active.png"))
        clearButton.setToolTipText UIUtils.getText(this.class, "clear")
        clearButton.name = "searchButton"
        reportAction = new CommentReportAction(model)
        exportToPdf = new ULCButton(reportAction)
        searchText = new ULCTextField(name: "searchText")
        searchText.setMaximumSize(new Dimension(20, 180))
        searchText.setToolTipText UIUtils.getText(this.class, "initialText")
        searchText.setText(UIUtils.getText(this.class, "initialText"))
        searchText.setForeground(Color.gray)
        searchText.setPreferredSize(new Dimension(200, 20))
        orderByLabel = new ULCLabel(UIUtils.getText(this.class, "orderBy"))
        orderByComboBoxModel = new MapComboBoxModel(["lastChange", "path", "user"])
        orderByComboBox = new ULCComboBox(orderByComboBoxModel)
        orderByComboBox.setMinimumSize(new Dimension(80, 20))
        orderComboBoxModel = new MapComboBoxModel(["desc", "asc"])
        orderComboBox = new ULCComboBox(orderComboBoxModel)
        orderComboBox.setMinimumSize(new Dimension(80, 20))
    }

    protected void layoutComponents() {
        toolBar.add(UIUtils.spaceAround(selectValidationButton, 0, 2, 0, 2, null));
        toolBar.add(UIUtils.spaceAround(selectCommentButton, 0, 2, 0, 2, null))
        toolBar.add(ULCFiller.createHorizontalStrut(10))
        toolBar.addSeparator()
        toolBar.add(UIUtils.spaceAround(orderByLabel, 0, 5, 0, 5, null))
        toolBar.add(UIUtils.spaceAround(orderByComboBox, 0, 2, 0, 2, null));
        toolBar.add(UIUtils.spaceAround(orderComboBox, 0, 2, 0, 2, null));
        toolBar.add(ULCFiller.createHorizontalStrut(10))
        toolBar.addSeparator()
        toolBar.add(ULCFiller.createHorizontalStrut(10))
        toolBar.add(searchText);
        toolBar.add(clearButton);
        toolBar.add(exportToPdf);
        content.add(ULCBoxPane.BOX_LEFT_TOP, toolBar)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
//        toolBar.add( new ULCFiller())

    }

    protected void attachListeners() {
        selectCommentButton.addActionListener([actionPerformed: {ActionEvent event ->
            commentsView.setVisible(selectCommentButton.selected)
            resultView.setVisible(false)
        }] as IActionListener)
        selectValidationButton.addActionListener([actionPerformed: {ActionEvent event ->
            errorsView.setVisible(selectValidationButton.selected)
            resultView.setVisible false
        }] as IActionListener)

        Closure searchClosure = {ActionEvent event ->
            String text = searchText.getText()
            if (text) {
                List<Comment> comments = commentSearchBean.performSearch(text)
                commentsView.setVisible(false)
                selectCommentButton.selected = false
                errorsView.setVisible(false)
                selectValidationButton.selected = false
                resultView.setVisible true
                resultView.clear()
                resultView.comments = comments
                resultView.addComments(comments, text)
                reportAction.filteredComments = comments
            }
        }
        IActionListener action = [actionPerformed: {e -> searchClosure.call()}] as IActionListener
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        searchText.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        searchText.addFocusListener(new TextFieldFocusListener(searchText))

        orderByComboBox.addActionListener([actionPerformed: {ActionEvent event ->
            ULCComboBox source = (ULCComboBox) event.getSource();
            order(source.model.selectedObject, orderComboBox.model.selectedObject)
        }] as IActionListener)

        orderComboBox.addActionListener([actionPerformed: {ActionEvent event ->
            ULCComboBox source = (ULCComboBox) event.getSource();
            order(orderByComboBox.model.selectedObject, source.model.selectedObject)
        }] as IActionListener)

        clearButton.addActionListener([actionPerformed: {ActionEvent event ->
            searchText.setText(UIUtils.getText(this.class, "initialText"))
            searchText.setForeground Color.gray
            commentsView.setVisible(true)
            resultView.setVisible(false)
            reportAction.filteredComments = null
        }] as IActionListener)

    }

    private def order(String orderBy, String order) {
        if (commentsView.isVisible())
            commentsView.order(orderBy, order)
        if (resultView.isVisible())
            resultView.order(orderBy, order)
    }

    public void setVisible(boolean visible) {
        content.setVisible(visible)
    }
}


