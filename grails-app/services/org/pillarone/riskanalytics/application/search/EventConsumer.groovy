package org.pillarone.riskanalytics.application.search

import com.ulcjava.base.server.ULCSession

class EventConsumer implements IEventConsumer {
    final ULCSession session
    final Object consumer

    EventConsumer(ULCSession session, Object consumer) {
        this.session = session
        this.consumer = consumer
    }

    @Override
    String toString() {
        "$consumer $session"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        EventConsumer that = (EventConsumer) o

        if (consumer != that.consumer) return false
        if (session != that.session) return false

        return true
    }

    int hashCode() {
        int result
        result = (session != null ? session.hashCode() : 0)
        result = 31 * result + (consumer != null ? consumer.hashCode() : 0)
        return result
    }
}
