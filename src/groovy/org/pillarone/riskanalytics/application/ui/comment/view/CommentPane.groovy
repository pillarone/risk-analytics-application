package org.pillarone.riskanalytics.application.ui.comment.view

import org.pillarone.riskanalytics.core.FileConstants;


import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.FunctionComment

import be.devijver.wikipedia.Parser
import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.base.view.FollowLinkPane
import org.pillarone.riskanalytics.application.ui.comment.action.EditCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.MakeVisibleAction
import org.pillarone.riskanalytics.application.ui.comment.action.RemoveCommentAction
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.springframework.web.util.HtmlUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.base.action.DownloadFileAction
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentPane {
    ULCBoxPane content;
    FollowLinkPane label
    ULCBoxPane downloadFilePane
    ULCLabel tags
    ULCButton editButton
    ULCButton deleteButton
    ULCButton makeVisibleButton
    Comment comment
    String path
    int periodIndex
    EditCommentAction editCommentAction
    RemoveCommentAction removeCommentAction
    MakeVisibleAction makeVisibleAction
    protected AbstractCommentableItemModel model
    String searchText = null

    public CommentPane(AbstractCommentableItemModel model, Comment comment, String searchText = null) {
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
        downloadFilePane = new ULCBoxPane(3, 0)//new DownloadFilePane(source: content)
        downloadFilePane.setBackground(Color.white)
        if (searchText) label.name = "foundText"
        label.setText getLabelText()
        addCommentFiles()

        label.setFont(font);
        tags = new ULCLabel()
        tags.setText HTMLUtilities.convertToHtml(getTagsValue())
        editCommentAction = new EditCommentAction(comment)
        Closure enablingClosure = {->
            if (model instanceof ResultViewModel) return true
            if (comment.tags.any { it.name == NewCommentView.POST_LOCKING}) return true
            if (model?.isReadOnly()) return false
            if (model?.item?.isEditable()) return true
            return false
        }
        editCommentAction.enablingClosure = enablingClosure
        editButton = new ULCButton(editCommentAction)
        editButton.setContentAreaFilled false
        editButton.setBackground Color.white
        editButton.setOpaque false
        editButton.name = "editComment"
        removeCommentAction = new RemoveCommentAction(model, comment)
        removeCommentAction.enablingClosure = enablingClosure

        deleteButton = new ULCButton(removeCommentAction)
        deleteButton.setContentAreaFilled false
        editButton.setOpaque true
        deleteButton.name = "deleteComment"
        makeVisibleAction = new MakeVisibleAction(model, comment.path)
        makeVisibleButton = new ULCButton(makeVisibleAction)
        makeVisibleButton.setContentAreaFilled false
        makeVisibleButton.setBackground Color.white
        makeVisibleButton.setOpaque false
    }



    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, tags);
        ULCBoxPane buttons = new ULCBoxPane(3, 1)
        buttons.add(makeVisibleButton)
        buttons.add(editButton)
        buttons.add(deleteButton)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
        content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        content.add(3, ULCBoxPane.BOX_LEFT_TOP, downloadFilePane);
    }


    void addCommentListener(CommentListener listener) {
        editCommentAction.addCommentListener listener
        makeVisibleAction.addCommentListener listener
    }

    String getTagsValue() {
        return CommentUtils.getTagsValue(comment)
    }


    String getTitle() {
        return CommentUtils.getCommentTitle(comment, model)
    }

    private String getLabelText() {
        String text = comment.getText()
        if (searchText) {
            text = addHighlighting(text, searchText.split())
        }
        String wiki = null
        try {
            if (text) text = endLineToHtml(text)
            java.io.StringWriter writer = new java.io.StringWriter();
            (new Parser()).withVisitor(text, new HtmlVisitor(writer, null));
            wiki = writer.toString()
        } catch (Exception ex) {
            wiki = text
        }
        return HTMLUtilities.convertToHtml(HtmlUtils.htmlUnescape(wiki))
    }

    String getCommentFiles() {
        StringBuilder sb = new StringBuilder("<br>" + UIUtils.getText(NewCommentView, "addedFiles") + ":<br>")
        String url = FileConstants.COMMENT_FILE_DIRECTORY
        for (String file: comment.files) {
            sb.append("<a href='${url + File.separator + file}' >${file}</a><br>")
        }
        return sb.toString()
    }

    void addCommentFiles() {
        downloadFilePane.add(3, ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(UIUtils.getText(NewCommentView, "addedFiles") + ":"))
        String url = FileConstants.COMMENT_FILE_DIRECTORY
        for (String file: comment.files) {
            String fileName = url + File.separator + file
            downloadFilePane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(file))
            DownloadFileAction downloadFileAction = new DownloadFileAction(fileName, content, false)
            DownloadFileAction downloadFileAndOpen = new DownloadFileAction(fileName, content, true)
            ULCButton downloadButton = new ULCButton(downloadFileAction)
            ULCButton downloadAndOpenButton = new ULCButton(downloadFileAndOpen)
            render(downloadButton, "downloadButton")
            render(downloadAndOpenButton, "downloadAndOpenButton")
            downloadFilePane.add(ULCBoxPane.BOX_LEFT_TOP, downloadButton)
            downloadFilePane.add(ULCBoxPane.BOX_LEFT_TOP, downloadAndOpenButton)
        }
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


    private String endLineToHtml(String text) {
        // \n causes hiding of links
        //workaround: replace all endline with html code
        return text.replaceAll("\n", "<br>")
    }

    private void render(ULCButton button, String name) {
        button.setContentAreaFilled false
        button.setBackground Color.white
        button.setOpaque false
        button.name = name
    }


}
