package org.pillarone.riskanalytics.application.ui.base.action

import groovy.transform.CompileStatic

@CompileStatic
class CopyPasteException extends Exception {

    CopyPasteException(String s, Class targetType, String clipboardContent) {
        super(createMessage(s, targetType, clipboardContent))
    }

    CopyPasteException(String s, Class targetType, String clipboardContent, String message) {
        super(createMessage(s, targetType, clipboardContent, message))
    }

    private static String trim(String content) {
        if (content.length() > 100) {
            content = content.substring(0, 100) + "\n..."
        }

        return content.replace("\t", " | ")
    }

    private static String createMessage(String s, Class targetType, String clipboardContent) {
        return "Cannot paste value '$s' - expecting ${targetType.simpleName} \n\nClipboard:\n\n${trim(clipboardContent)}"
    }

    private static String createMessage(String s, Class targetType, String clipboardContent, String message) {
        return "Cannot paste value '$s' - expecting ${targetType.simpleName}. $message \n\nClipboard:\n\n${trim(clipboardContent)}"
    }
}
