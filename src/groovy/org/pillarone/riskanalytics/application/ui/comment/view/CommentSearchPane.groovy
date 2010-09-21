package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.comment.model.CommentSearchBean
import org.pillarone.riskanalytics.application.ui.comment.model.MapComboBoxModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
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
    ULCLabel orderByLabel
    ULCComboBox orderByComboBox
    ULCComboBox orderComboBox
    ShowCommentsView commentsView
    ErrorPane errorsView
    ShowCommentsView resultView
    CommentSearchBean commentSearchBean
    DefaultComboBoxModel orderByComboBoxModel
    DefaultComboBoxModel orderComboBoxModel

    public CommentSearchPane(ShowCommentsView commentsView, ErrorPane errorsView, ShowCommentsView resultView, model) {
        this.model = model;
        this.commentsView = commentsView
        this.errorsView = errorsView
        this.resultView = resultView
        this.resultView.setVisible false
        this.commentSearchBean = new CommentSearchBean(this.model.item.comments)
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        content = new ULCBoxPane(8, 1);
        content.name = "CommentSearchPane"
        content.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder("");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);
        selectCommentButton = new ULCToggleButton("All comments")
        selectCommentButton.setSelected(true)
        selectValidationButton = new ULCToggleButton("Validations")
        selectValidationButton.setSelected(true)
        searchButton = new ULCButton("Search")
        searchButton.name = "searchButton"
        searchText = new ULCTextField(name: "searchText")
        searchText.setToolTipText "search text"
        searchText.setPreferredSize(new Dimension(200, 20))
        orderByLabel = new ULCLabel("order by")
        orderByComboBoxModel = new MapComboBoxModel(["path", "user", "lastChange"])
        orderByComboBox = new ULCComboBox(orderByComboBoxModel)
        orderComboBoxModel = new MapComboBoxModel(["asc", "desc"])
        orderComboBox = new ULCComboBox(orderComboBoxModel)
    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, selectValidationButton);
        content.add(ULCBoxPane.BOX_LEFT_TOP, selectCommentButton)
        content.add(ULCBoxPane.BOX_LEFT_TOP, searchText);
        content.add(ULCBoxPane.BOX_LEFT_TOP, searchButton);
        content.add(ULCBoxPane.BOX_LEFT_CENTER, UIUtils.spaceAround(orderByLabel, 0, 5, 0, 5, null));
        content.add(ULCBoxPane.BOX_LEFT_TOP, orderByComboBox);
        content.add(ULCBoxPane.BOX_LEFT_TOP, orderComboBox);
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
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

        searchButton.addActionListener([actionPerformed: {ActionEvent event ->
            String text = searchText.getText()
            if (text) {
                List<Comment> comments = commentSearchBean.performSearch(text)
                commentsView.setVisible(false)
                selectCommentButton.selected = false
                errorsView.setVisible(false)
                selectValidationButton.selected = false
                resultView.setVisible true
//                if (comments) {
                resultView.clear()
                resultView.comments = comments
                resultView.addComments(comments)
//                } else {
//                    resultView.clear()
//                }
            }


        }] as IActionListener)
        orderByComboBox.addActionListener([actionPerformed: {ActionEvent event ->
            ULCComboBox source = (ULCComboBox) event.getSource();
            order(source.model.selectedObject, orderComboBox.model.selectedObject)
        }] as IActionListener)

        orderComboBox.addActionListener([actionPerformed: {ActionEvent event ->
            ULCComboBox source = (ULCComboBox) event.getSource();
            order(orderByComboBox.model.selectedObject, source.model.selectedObject)
        }] as IActionListener)
    }

    private def order(String orderBy, String order) {
        if (commentsView.isVisible())
            commentsView.order(orderBy, order)
        if (resultView.isVisible())
            resultView.order(orderBy, order)
    }
}
