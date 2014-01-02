package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCRootPane
import grails.util.Holders
import org.pillarone.riskanalytics.core.log.TraceLogManager

import java.text.MessageFormat
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.runtime.StackTraceUtils
import org.pillarone.riskanalytics.core.parameterization.ParameterizationError
import org.pillarone.riskanalytics.core.workflow.WorkflowException
import org.pillarone.riskanalytics.application.ui.base.action.CopyPasteException
import org.pillarone.riskanalytics.core.user.UserManagement
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.FileConstants

import java.text.SimpleDateFormat

/**
 * Convenience class to allow easy use of protect  {}  for centralized Exception handling.
 * Stack trace logging can be achieved by setting the static <i>out</i> property to anything
 * that responds to the leftShift operator (Writer, Stream, StringBuffer, etc.). But
 * <b>be careful</b> since playing with this property will affect all users in all threads!
 * {@see ExceptionSafeTest}
 */

class ExceptionSafe {
    private static LOG = Logger.getLogger(ExceptionSafe)
    private static messagePatterns = [
            Exception: "PillarOne has hit a technical error, please contact application support\n(details regarding the error have been written to the log files).",
            IllegalStateException: "{0}\nMore details may be found in logs.",
            RiskAnalyticsInconsistencyException: "An internal consistency check has failed: \n{0}\nMore details available in the log files.",
            ParameterizationSaveError: "Invalid parameterization:\n\n {0}\n\nThe parameterization is not saved.",
            ParameterizationImportError: "Error in parameterization on line {1}:\n\n{0}\n\nParameterization import failed.",
            PasteError: "Paste Error:\n\n {0} \nThis error appears also if the tree structure changes while pasting.",
            MissingHelpException: "No help available",
            HibernateOptimisticLockingFailureException: "Your values cannot be stored because another\nuser has changed them in the meantime.",
            WorkflowException: "Workflow action failed. Reason: {0}",
            CopyPasteException: "Unable to paste at least one value: {0}"
    ]

    private static titles = [
            Exception: "Notification",
            IllegalStateException: "Runtime error",
            RiskAnalyticsInconsistencyException: "Internal error",
            ParameterizationSaveError: "Parameterization error",
            ParameterizationImportError: "Parameterization error",
            PasteError: "Paste error",
            MissingHelpException: "Notification",
            HibernateOptimisticLockingFailureException: "Concurrent modification",
            WorkflowException: "Workflow error",
            CopyPasteException: "Paste error"
    ]

    private static errorLevel = [
            Exception: ULCAlert.ERROR_MESSAGE,
            IllegalStateException: ULCAlert.ERROR_MESSAGE,
            RiskAnalyticsInconsistencyException: ULCAlert.ERROR_MESSAGE,
            ParameterizationSaveError: ULCAlert.WARNING_MESSAGE,
            ParameterizationImportError: ULCAlert.WARNING_MESSAGE,
            PasteError: ULCAlert.INFORMATION_MESSAGE,
            MissingHelpException: ULCAlert.INFORMATION_MESSAGE,
            HibernateOptimisticLockingFailureException: ULCAlert.WARNING_MESSAGE,
            WorkflowException: ULCAlert.INFORMATION_MESSAGE,
            CopyPasteException: ULCAlert.INFORMATION_MESSAGE
    ]

    static ULCRootPane rootPane = null

    static final List IGNORE = [
            /^sun\./,
            /^com\.intellij\./,
            /^java\./,
            /^groovy\./,
            /^org\.codehaus\./,
            /^com\.ulcjava\./,
            /^org\.pillarone\.modelling\.ui\.util\.ExceptionSafe$/,
            /^junit\./
    ]

    static def protect(Closure yield) {
        try {
            return yield()
        } catch (Exception e) {
            logException(e, yield.delegate)
            handleException e
        }
    }

    static void handleException(Exception e) {
        try {
            showErrorAlert(e)
        } catch (ignore) { /* if the alert cannot be shown, there is nothing we can do... */ } //TODO Why not at least write something to the log file ?
    }

    public static def logException(Exception e, def context) {
        saveError(e)
        // 20131220 frahman TODO figure out why IDEA console NOT showing expected error:
        // java.lang.IllegalArgumentException: No enum constant com.allianz.art.riskanalytics.pc.generators.claims.rms.RmsEventSet.V13_STANDARD_MEDIUMTERM
        Throwable sanitized = StackTraceUtils.deepSanitize(e)
        def logMessage = "${context.class.name} caused ${sanitized.class.name}: ${sanitized.message}"
        LOG.error(logMessage)
        LOG.error("\n" + niceStackTrace(e))
    }

    public static void saveError(Throwable e) {
        String user = UserManagement.currentUser?.username ?: "local"
        String time = new SimpleDateFormat('yyyy-MM-dd_HH-mm-ss-z').format(new Date())
        String filename = "error-" + user + "-${time}.log"

        StringBuilder text = new StringBuilder("Stack trace:\n\n")
        text.append(e).append('\n')
        text.append(niceStackTrace(e)).append("\n\n\nLog:\n\n")

        TraceLogManager traceLogManager = Holders.grailsApplication.mainContext.getBean(TraceLogManager)
        text.append(traceLogManager.trace?.join(""))
        traceLogManager.clear()

        File file = new File(FileConstants.LOG_DIRECTORY + "/" + filename)
        file.text = text.toString()
    }


    private static def showErrorAlert(Exception e) {
        String key = e.class.simpleName
        if (!titles.containsKey(key)) {
            key = Exception.class.simpleName
        }
        String title = titles[key]
        String text = getMessage(e, key)
        ULCAlert alert = new ULCAlert(UIUtils.getRootPane(), title, text, "OK")
        alert.messageType = errorLevel[key]
        alert.show()
    }

    protected static String getMessage(Exception e, String key) {
        return new MessageFormat(messagePatterns[key]).format([e.message] as Object[])
    }

    protected static String getMessage(ParameterizationError e, String key) {
        return new MessageFormat(messagePatterns[key]).format([e.message, e.lineNumber] as Object[])
    }


    private static String niceStackTrace(Throwable e) {
        def stackTraceElements = e.stackTrace.findAll { frame -> !IGNORE.any { frame.className =~ it } }
        return stackTraceElements.join("\n")
    }
}
