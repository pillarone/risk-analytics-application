package org.pillarone.riskanalytics.application.ui.upload.queue.model

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfoAdapter
import org.pillarone.riskanalytics.core.user.Person
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadInfoPaneModel {
    private static final Log LOG = LogFactory.getLog(UploadInfoPaneModel)

    private DateTimeFormatter dateFormat = DateFormatUtils.getDateFormat("HH:mm")

    private UploadRuntimeInfo running

    @Resource
    UlcUploadRuntimeService ulcUploadRuntimeService

    @Delegate
    private UploadStateEventSupport support = new UploadStateEventSupport()
    private MyListener listener = new MyListener()

    @PostConstruct
    void register() {
        ulcUploadRuntimeService.addRuntimeInfoListener(listener)
    }

    @PreDestroy
    void unregister() {
        ulcUploadRuntimeService.removeRuntimeInfoListener(listener)
    }

    String getEstimatedEndTime() {
        DateTime uploadStartTime = running?.estimatedEnd
        if (uploadStartTime != null) {
            return dateFormat.print(uploadStartTime)
        }
        return "-"
    }

    String getUploadStartTime() {
        DateTime uploadStartTime = running?.start
        if (uploadStartTime != null) {
            return dateFormat.print(uploadStartTime)
        }
        return "-"
    }

    String getUploadEndTime() {
        DateTime uploadEndTime = running?.end
        if (uploadEndTime != null) {
            return dateFormat.print(uploadEndTime)
        }
        return "-"
    }

    String getRemainingTime() {
        "-"
    }

    int getProgress() {
        Integer pro = running?.progress ?: 0
        LOG.debug("updating progress to $pro")
        pro
    }

    String getErrorMessage() {
        List<String> errors = running?.uploadErrors
        if (!errors) {
            return ''
        }
        HashSet<String> messages = new HashSet<String>(errors);

        StringBuffer text = new StringBuffer();
        for (String exceptionMessage : messages) {
            List words = exceptionMessage.split(' ') as List
            int lineLength = 0
            for (String s in words) {
                if (lineLength + s.length() > 70) {
                    text << '\n'
                    lineLength = 0
                }
                text << s + ' '
                lineLength += (s.length() + 1)
            }
            text << '\n';
        }
        text.toString()
    }

    Person getUploadOwner() {
        running?.offeredBy
    }

    private class MyListener extends UploadRuntimeInfoAdapter {
        @Override
        void starting(UploadRuntimeInfo info) {
            running = info
            notifyUploadStateChanged(info.uploadState)
        }

        @Override
        void finished(UploadRuntimeInfo info) {
            running = info
            notifyUploadStateChanged(info.uploadState)
        }

        @Override
        void changed(UploadRuntimeInfo info) {
            running = info
            LOG.debug("info changed: progress is ${info.progress}")
            notifyUploadStateChanged(info.uploadState)
        }

    }
}
