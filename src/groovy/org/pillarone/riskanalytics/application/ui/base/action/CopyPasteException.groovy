package org.pillarone.riskanalytics.application.ui.base.action


class CopyPasteException extends Exception {

    CopyPasteException(String s, Class targetType) {
        super("Cannot paste value '$s' - expecting ${targetType.simpleName}".toString())
    }

    CopyPasteException(String s, Class targetType, String message) {
        super("Cannot paste value '$s' - expecting ${targetType.simpleName}. $message".toString())

    }
}
