package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession

interface IEventConsumer {
    ULCSession getSession()
    Object getConsumer()
}
