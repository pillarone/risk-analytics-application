package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.apache.log4j.spi.LoggingEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
interface LoggingListener {
    void append(LoggingEvent loggingEvent)
}
