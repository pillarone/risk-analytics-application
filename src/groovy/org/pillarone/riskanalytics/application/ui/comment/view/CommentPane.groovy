package org.pillarone.riskanalytics.application.ui.comment.view

import be.devijver.wikipedia.Parser
import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.ui.base.view.FollowLinkPane
import org.pillarone.riskanalytics.application.ui.comment.action.EditCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.RemoveCommentAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.springframework.web.util.HtmlUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentPane {
    private ULCBoxPane content;
    FollowLinkPane label
    ULCLabel tags
    ULCButton editButton
    ULCButton deleteButton
    Comment comment
    String path
    int periodIndex
    EditCommentAction editCommentAction
    RemoveCommentAction removeCommentAction
    protected ParameterViewModel model
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat('dd.MM.yyyy HH:mm')
    String searchText = null

    public CommentPane(ParameterViewModel model, Comment comment, String searchText = null) {
        this.model = model
        this.comment = comment
        if (searchText) this.searchText = searchText
        initComponents()
        layoutComponents()
    }


    protected void initComponents() {
        content = new ULCBoxPane(3, 2);
        content.setMinimumSize new Dimension(400, 100)
        content.name = "CommentPane"
        content.setBackground(Color.white);
        final ULCTitledBorder border = BorderFactory.createTitledBorder(getTitle());
        Font font = border.getTitleFont().deriveFont(Font.PLAIN)
        border.setTitleFont(font);
        content.setBorder(border);

        label = new FollowLinkPane();
        if (searchText) label.name = "foundText"
        label.setText getLabelText()

        label.setFont(font);
        tags = new ULCLabel()
        tags.setText HTMLUtilities.convertToHtml(getTagsValue())
        editCommentAction = new EditCommentAction(comment)
        editButton = new ULCButton(editCommentAction)
        editButton.setContentAreaFilled false
        editButton.setBackground Color.white
        editButton.setOpaque false
        editButton.name = "editComment"
        removeCommentAction = new RemoveCommentAction(model, comment)
        deleteButton = new ULCButton(removeCommentAction)
        deleteButton.setContentAreaFilled false
        editButton.setOpaque true
        deleteButton.name = "deleteComment"

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
        StringBuilder sb = new StringBuilder(UIUtils.getText(this.class, "Tags") + ":")
        comment.getTags().eachWithIndex {Tag tag, int index ->
            sb.append(tag.getName())
            if (index < size - 1)
                sb.append(", ")
        }
        return sb.toString()
    }

    String getTitle() {
        String username = comment.user ? comment.user.username : ""
        StringBuilder sb = new StringBuilder(CommentAndErrorView.getDisplayPath(model, comment.getPath()))
        sb.append((comment.getPeriod() != -1) ? " P" + comment.getPeriod() : " " + UIUtils.getText(CommentAndErrorView.class, "forAllPeriods"))
        sb.append(" " + username)
        sb.append(" " + simpleDateFormat.format(comment.lastChange))
        return sb.toString()
    }

    private String getLabelText() {
        String text = comment.getText()
        if (searchText) {
            text = addHighlighting(text, searchText.split())
        }
        String wiki = null
        try {
            java.io.StringWriter writer = new java.io.StringWriter();
            (new Parser()).withVisitor(text, new HtmlVisitor(writer, null));
            wiki = writer.toString()
        } catch (Exception ex) {
            wiki = text
        }
        return HTMLUtilities.convertToHtml(HtmlUtils.htmlUnescape(wiki))
    }

    private String addHighlighting(String text, def words) {
        def found = []
        words.each {
            (text =~ /(?i)${it}/).each {def m ->
                found.add(m)
            }
        }
        found.each {
            text = text.replaceAll(it, "<span style=\"font-weight:bold;color:#006400\">${it}</span>")
        }
        return text
    }


}
