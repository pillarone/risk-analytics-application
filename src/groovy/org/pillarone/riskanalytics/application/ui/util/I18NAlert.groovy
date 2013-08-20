package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCRootPane
import groovy.transform.CompileStatic

import java.text.MessageFormat
import org.pillarone.riskanalytics.application.util.LocaleResources

@CompileStatic
public class I18NAlert extends ULCAlert {
    String key
    List<String> args

    public I18NAlert(String key) {
        this(null, key, null)
    }

    public I18NAlert(ULCRootPane parent, String key) {
        this(parent, key, null)
    }

    public I18NAlert(ULCRootPane parent, String key, List<String> args) {
        super(parent as ULCRootPane, "", "", "")
        this.@key = key
        this.args = args
        readValues()
    }


    protected readValues() {
        title = bundle.getString(key + "." + "title")
        String rawMessage = bundle.getString(key + "." + "message")
        message = formatMessage(rawMessage, args)
        firstButtonLabel = bundle.getString(key + "." + "button1Message")
        String secondLabel = bundle.getString(key + "." + "button2Message")
        String thirdLabel = bundle.getString(key + "." + "button3Message")
        if (secondLabel != null && !secondLabel.equals("")) {
            secondButtonLabel = secondLabel
        }
        if (thirdLabel != null && !thirdLabel.equals("")) {
            thirdButtonLabel = thirdLabel
        }

        messageType = getAlertType()

    }

    static final String formatMessage(String rawMessage, List<String> args) {
        MessageFormat.format(rawMessage, args as String[])
    }

    protected ResourceBundle getBundle() {
        return LocaleResources.getBundle("org.pillarone.riskanalytics.application.alertResources");
    }

    protected int getAlertType() {
        String alertType = bundle.getString(key + "." + "alertType")
        int type = 0
        switch (alertType) {
            case "ERROR":
                type = ERROR_MESSAGE
                break
            case "INFORMATION":
                type = INFORMATION_MESSAGE
                break
            case "PLAIN":
                type = PLAIN_MESSAGE
                break
            case "QUESTION":
                type = QUESTION_MESSAGE
                break
            case "WARNING":
                type = WARNING_MESSAGE
                break
            default:
                throw new Exception("wrong alert type in resource bundle")
        }
        return type
    }

}