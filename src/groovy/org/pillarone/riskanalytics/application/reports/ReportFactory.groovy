package org.pillarone.riskanalytics.application.reports

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.ReportUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.Person

public class ReportFactory {
    public static String REPORT_DIR = '/reports'
    public static String SERVER_REPORT_DIR = '/reports'

    public static boolean testMode = false
    public static boolean generationSuccessful = false


    static def getReport(JRBeanCollectionDataSource collectionDataSource, ModellingItem modellingItem) {

        Map params = new HashMap()
        params["comments"] = collectionDataSource
        params["title"] = UIUtils.getText(ReportFactory.class, "title", [ReportUtils.getItemName(modellingItem)] as List)
        Person currentUser = UserContext.getCurrentUser()
        String footerValue = currentUser ? UIUtils.getText(ReportFactory.class, "footerByUser", [currentUser.username]) : UIUtils.getText(ReportFactory.class, "footer")
        footerValue += " " + DateFormatUtils.formatDetailed(new DateTime())
        params["footer"] = footerValue
        params["infos"] = ReportUtils.getItemInfo(modellingItem)
        params["currentUser"] = currentUser ? currentUser.username : ""
        params["itemInfo"] = UIUtils.getText(ReportFactory.class, modellingItem.class.simpleName + "Info")
        params["_file"] = "CommentReport"
        params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
        params["Comment"] = "Comment"
        params["p1Icon"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "application.png")
        params["p1Logo"] = new UIUtils().class.getResource(UIUtils.ICON_DIRECTORY + "pdf-reports-header.png")
        return ReportHelper.getReportOutputStream(params, collectionDataSource).toByteArray()
    }

}