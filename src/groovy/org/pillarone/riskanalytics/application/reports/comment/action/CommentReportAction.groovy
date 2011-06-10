package org.pillarone.riskanalytics.application.reports.comment.action

import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.reports.ReportFactory
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.shared.FileChooserConfig
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.ULCAlert
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.reports.AbstractReportAction
import static org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView.*
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.CommentUtils
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.util.UIUtils

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
        return model.item.name + ".pdf"
    }

}
