package org.pillarone.riskanalytics.application.ui

import com.ulcjava.container.local.server.LocalContainerAdapter
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.P1RATApplication

class P1RATStandaloneRunner extends LocalContainerAdapter {

    protected Class getApplicationClass() {
        P1RATApplication
    }
}