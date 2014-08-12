package org.pillarone.riskanalytics.application.ui.upload.queue.model

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.queue.IRuntimeInfoListener
import org.pillarone.riskanalytics.core.queue.RuntimeInfoEventSupport
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.pillarone.riskanalytics.core.upload.UploadRuntimeService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UlcUploadRuntimeService {

    @Resource
    UploadRuntimeService uploadRuntimeService
    @Resource(name = 'pollingSupport2000')
    PollingSupport pollingSupport
    private MyRuntimeEventListener runtimeEventListener
    private IActionListener pollingListener
    private List<UploadRuntimeInfoEvent> events = []
    private final Object eventsLock = new Object()
    @Delegate
    private final RuntimeInfoEventSupport<UploadRuntimeInfo> eventSupport = new RuntimeInfoEventSupport<UploadRuntimeInfo>()

    @PostConstruct
    void register() {
        pollingListener = new MyActionListener()
        runtimeEventListener = new MyRuntimeEventListener()
        uploadRuntimeService.addRuntimeInfoListener(runtimeEventListener)
        pollingSupport.addActionListener(pollingListener)
    }

    @PreDestroy
    private void unregister() {
        pollingSupport.removeActionListener(pollingListener)
        uploadRuntimeService.removeRuntimeInfoListener(runtimeEventListener)
    }


    private void fireEvents() {
        List currentEvents
        synchronized (eventsLock) {
            currentEvents = events
            events = []
        }
        currentEvents.each { UploadRuntimeInfoEvent event ->
            UploadRuntimeInfo info = event.info
            switch (event.type) {
                case UploadRuntimeInfoEvent.TYPE.OFFERED:
                    offered(info)
                    break
                case UploadRuntimeInfoEvent.TYPE.STARTING:
                    starting(info)
                    break
                case UploadRuntimeInfoEvent.TYPE.FINISHED:
                    finished(info)
                    break
                case UploadRuntimeInfoEvent.TYPE.REMOVED:
                    removed(info)
                    break
                case UploadRuntimeInfoEvent.TYPE.CHANGED:
                    changed(info)
                    break
            }
        }
    }

    private class MyRuntimeEventListener implements IRuntimeInfoListener<UploadRuntimeInfo> {
        @Override
        void starting(UploadRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new UploadRuntimeInfoEvent(type: UploadRuntimeInfoEvent.TYPE.STARTING, info: info)
            }
        }

        @Override
        void finished(UploadRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new UploadRuntimeInfoEvent(type: UploadRuntimeInfoEvent.TYPE.FINISHED, info: info)
            }

        }

        @Override
        void removed(UploadRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new UploadRuntimeInfoEvent(type: UploadRuntimeInfoEvent.TYPE.REMOVED, info: info)
            }
        }

        @Override
        void offered(UploadRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new UploadRuntimeInfoEvent(type: UploadRuntimeInfoEvent.TYPE.OFFERED, info: info)
            }
        }

        @Override
        void changed(UploadRuntimeInfo info) {
            synchronized (eventsLock) {
                events << new UploadRuntimeInfoEvent(type: UploadRuntimeInfoEvent.TYPE.CHANGED, info: info)
            }
        }
    }

    private class MyActionListener implements IActionListener {
        @Override
        void actionPerformed(ActionEvent event) {
            fireEvents()
        }
    }

    static class UploadRuntimeInfoEvent {
        enum TYPE {
            OFFERED, STARTING, FINISHED, REMOVED, CHANGED
        }
        UploadRuntimeInfo info
        TYPE type
    }

}
