package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.border.ULCTitledBorder
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Font
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.FileConstants;
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.comment.action.AddFileToCommentAction
import org.pillarone.riskanalytics.application.ui.comment.model.ItemListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewCommentView {
    ULCBoxPane content
    ULCTextArea commentTextArea
    ULCList tags
    ULCButton addButton
    ULCButton addFileButton
    ULCButton cancelButton
    final Dimension dimension = new Dimension(140, 20)
    int periodIndex
    String path
    static int MAX_CHARS = 4080


    AbstractCommentableItemModel model;
    protected CommentAndErrorView commentAndErrorView
    ItemListModel<Tag> tagListModel
    AddFileToCommentAction addFileToCommentAction
    List<CommentFile> addedFiles = []
    ULCBoxPane addedFilesPane
    ULCLabel addedFilesLabel
    Log LOG = LogFactory.getLog(NewCommentView)

    final static String POST_LOCKING = "post locking"
    final static String SHARED_COMMENTS = "shared comment"
    final static String VERSION_COMMENT = "version"
    final static String REPORT = "report"

    public NewCommentView() {}

    public NewCommentView(CommentAndErrorView commentAndErrorView) {
        this.commentAndErrorView = commentAndErrorView
        this.model = commentAndErrorView.model
        def tags = allTags
        this.tagListModel = new ItemListModel<Tag>(tags?.collect { it.name }.toArray(), tags)
    }

    public NewCommentView(CommentAndErrorView commentAndErrorView, String path, int periodIndex) {
        this(commentAndErrorView)
        this.periodIndex = periodIndex
        this.path = path

        initComponents()
        layoutComponents()
        attachListeners()
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        commentTextArea = new ULCTextArea(5, 45)
        commentTextArea.setMinimumSize(new Dimension(200, 160))
        commentTextArea.setMaximumSize(new Dimension(400, 160))
        commentTextArea.name = "newCommentText"
        commentTextArea.lineWrap = true
        commentTextArea.wrapStyleWord = true

        tags = new ULCList(tagListModel)
        tags.name = "tagsList"
        tags.setVisibleRowCount(6);
        tags.setMinimumSize(new Dimension(100, 160))

        addButton = new ULCButton(UIUtils.getText(NewCommentView.class, "Apply"))
        addButton.name = "saveNewComment"
        addButton.setPreferredSize(dimension)

        addFileToCommentAction = new AddFileToCommentAction(this)
        addFileButton = new ULCButton(addFileToCommentAction)
        addFileButton.name = "addFileButton"
        addFileButton.setPreferredSize(dimension)

        addedFilesPane = new ULCBoxPane(2, 0)
        addedFilesPane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(UIUtils.getText(NewCommentView, "addedFiles") + ": "))
        addedFilesLabel = new ULCLabel("-")
        addedFilesPane.add(addedFilesLabel)

        cancelButton = new ULCButton(UIUtils.getText(NewCommentView.class, "Cancel"))
        cancelButton.name = "cancelComment"
        cancelButton.setPreferredSize(dimension)
        content = new ULCBoxPane(3, 3)
        content.setMinimumSize(new Dimension(400, 160))
        final ULCTitledBorder border = BorderFactory.createTitledBorder(getContentBorderTitle());
        border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
        content.setBorder(border);
    }

    String getContentBorderTitle() {
        return getDisplayPath() + ((periodIndex == -1) ? " " + UIUtils.getText(this.class, "forAllPeriods") : " P" + periodIndex)
    }

    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_TOP, UIUtils.spaceAround(new ULCScrollPane(commentTextArea), 5, 5, 0, 0))
        ULCScrollPane scrollList = new ULCScrollPane(tags)
        content.add(ULCBoxPane.BOX_LEFT_TOP, UIUtils.spaceAround(scrollList, 5, 0, 0, 0))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
        content.add(3, ULCBoxPane.BOX_LEFT_TOP, addedFilesPane)
        content.add(2, ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(1, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());

        content.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller());
    }

    protected ULCBoxPane getButtonsPane() {
        ULCBoxPane pane = new ULCBoxPane(3, 1)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, addFileButton)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, addButton)
        pane.add(ULCBoxPane.BOX_LEFT_BOTTOM, cancelButton)
        return pane
    }

    protected Comment createComment(String path, int periodIndex, String function = null) {
        return new Comment(path, periodIndex)
    }

    protected void attachListeners() {
        addButton.addActionListener([actionPerformed: { ActionEvent evt ->
            addCommentToItem(path, periodIndex)
            commentAndErrorView.closeTab()
        }] as IActionListener)

        cancelButton.addActionListener([actionPerformed: { ActionEvent evt ->
            commentAndErrorView.closeTab()
        }] as IActionListener)

    }

    protected void addCommentToItem(String path, int periodIndex, String function = null) {
        String text = commentTextArea.getText()
        if (text && text.length() > 0 && text.length() < MAX_CHARS) {
            Comment comment = createComment(path, periodIndex, function)
            addedFiles.each { f -> comment.addFile(f) }

            comment.lastChange = new DateTime()
            comment.comment = commentTextArea.getText()
            tagListModel.getSelectedValues(tags.getSelectedIndices()).each { Tag tag ->
                comment.addTag(tag)
            }
            addPostTag(comment)
            model.addComment(comment)
            saveComments(model.item)

        } else if (text && text.length() > MAX_CHARS) {
            new I18NAlert("CommentTooLong").show()
        } else {
            new I18NAlert("CommentIsNull").show()
        }
    }

    public static List getAllTags() {
        Tag.createCriteria().list {
            ne('name', POST_LOCKING)
            ne('name', SHARED_COMMENTS)
            eq('tagType', EnumTagType.COMMENT)
        }
    }

    String getDisplayPath() {
        return CommentAndErrorView.getDisplayPath(model, path)
    }

    /**
     * add post locking only for comment
     * @param comment
     */
    protected void addPostTag(Comment comment) {
        if ((model instanceof ParameterViewModel) && comment.class.isAssignableFrom(Comment) && model.isReadOnly()) {
            Tag postLocking = Tag.findByName(POST_LOCKING)
            if (!comment.tags.contains(postLocking))
                comment.addTag(postLocking)
        }
    }

    protected void saveComments(Simulation simulation) {
        simulation.save()
    }

    protected void saveComments(def item) {
    }

    public fileAdded(CommentFile file) {
        if (addedFiles.contains(file)) return
        addedFiles.add(file)
        ULCLabel label = new ULCLabel(file.filename)
        addedFilesPane.add(label)
        ULCButton removeFileButton = new ULCButton(UIUtils.getIcon("cancel.png"))
        removeFileButton.name = "removeFileButton"
        removeFileButton.setContentAreaFilled false
        removeFileButton.setBackground Color.white
        removeFileButton.setOpaque false
        removeFileButton.setToolTipText(UIUtils.getText(NewCommentView, "removeFile", [file.filename]))
        removeFileButton.addActionListener([actionPerformed: { ActionEvent event ->
            addedFiles.remove(file)
            addedFilesPane.remove(label)
            addedFilesPane.remove(removeFileButton)
        }] as IActionListener)
        addedFilesPane.add(removeFileButton)
    }
}
