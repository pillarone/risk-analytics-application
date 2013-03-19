package org.pillarone.riskanalytics.application.reports.comment.action

import com.ulcjava.base.application.event.ActionEvent
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.reports.AbstractReportAction
import org.pillarone.riskanalytics.application.reports.ReportFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.FileConstants

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentReportAction extends AbstractReportAction {

    AbstractCommentableItemModel model
    List<Comment> filteredComments


    Log LOG = LogFactory.getLog(CommentReportAction)

    public CommentReportAction(AbstractCommentableItemModel model) {
        super("CommentReportAction")
        this.model = model

    }

    @Override
    void doActionPerformed(ActionEvent event) {
        try {
            saveReport(getReport(), getFileName(), event?.source)
        } catch (IllegalArgumentException e) {
            LOG.error "Can not create report: ${e.message} Stacktrace: ${e.stackTrace}"
        }
    }

    public def getReport() {
        return ReportFactory.getReport(getCollectionDataSource(), model?.item)
    }

    public JRBeanCollectionDataSource getCollectionDataSource() {
        Collection currentValues = new ArrayList<Comment>()
        for (Comment comment: getComments()) {
            addCommentData(comment, currentValues)
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    public void addCommentData(Comment comment, Collection currentValues) {
        String boxTitle = CommentUtils.getCommentTitle(comment, model)
        String tags = CommentUtils.getTagsValue(comment).replaceAll("<br>", ", ")
        String addedFiles = UIUtils.getText(CommentReportAction.class, "attachments") + ": " + (comment.getFiles() as List).join(", ")
        currentValues << ["boxTitle": boxTitle, "tags": tags, "addedFiles": addedFiles, "text": comment.getText()]
    }

    List<Comment> getComments() {
        filteredComments ? filteredComments : model.item.comments
    }

    String getFileName() {
        return validateFileName(model.item.name) + System.currentTimeMillis() + ".pdf"
    }

    @Override
    String getTargetDir() {
        return FileConstants.COMMENT_PDF_DIRECTORY
    }

}
