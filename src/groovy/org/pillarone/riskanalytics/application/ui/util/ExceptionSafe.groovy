package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCRootPane
import java.text.MessageFormat
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.runtime.StackTraceUtils
import org.pillarone.riskanalytics.core.parameterization.ParameterizationError

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
        Exception: "Sorry, PillarOne has hit a technical error.\n[{0}]\n\nIt tries to proceed, but it is safer to restart PillarOne.\nYou may want to save your data before restarting.",
        ParameterizationSaveError: "Invalid parameterization:\n\n {0}\n\nThe parameterization is not saved.",
        ParameterizationImportError: "Error in parameterization on line {1}:\n\n{0}\n\nParameterization import failed.",
        PasteError: "Paste Error:\n\n {0} \nThis error appears also if the tree structure changes while pasting.",
        MissingHelpException: "No help available",
        HibernateOptimisticLockingFailureException: "Your values cannot be stored because another\nuser has changed them in the meantime.",
    ]

    private static titles = [
        Exception: "Notification",
        ParameterizationSaveError: "Parameterization error",
        ParameterizationImportError: "Parameterization error",
        PasteError: "Paste error",
        MissingHelpException: "Notification",
        HibernateOptimisticLockingFailureException: "Concurrent modification",
    ]

    private static errorLevel = [
        Exception: ULCAlert.ERROR_MESSAGE,
        ParameterizationSaveError: ULCAlert.WARNING_MESSAGE,
        ParameterizationImportError: ULCAlert.WARNING_MESSAGE,
        PasteError: ULCAlert.INFORMATION_MESSAGE,
        MissingHelpException: ULCAlert.INFORMATION_MESSAGE,
        HibernateOptimisticLockingFailureException: ULCAlert.WARNING_MESSAGE
    ]

    static ULCRootPane rootPane = null

    static out = ApplicationHolder.getApplication()?.config?.ExceptionSafeOut instanceof OutputStream ? ApplicationHolder.getApplication().config.ExceptionSafeOut : System.out

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
        } catch (ignore) { /* if the alert cannot be shown, there is nothing we can do... */}
    }

    public static def logException(Exception e, def context) {
        printStackTrace e
        Throwable sanitized = StackTraceUtils.deepSanitize(e)
        sanitized.printStackTrace()
        def logMessage = "${context.class.name} caused ${sanitized.class.name}: ${sanitized.message}"
        LOG.error(logMessage)
        LOG.debug(context, sanitized)
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


    private static void printStackTrace(Exception e) {
        if (null != out) {
            def stackTraceElements = e.stackTrace.findAll {frame -> !IGNORE.any {frame.className =~ it}}
            synchronized (out) {
                out << e.message
                out << "\n"
                out << stackTraceElements.join("\n")
                out << "\n"
            }
        }
    }
}
