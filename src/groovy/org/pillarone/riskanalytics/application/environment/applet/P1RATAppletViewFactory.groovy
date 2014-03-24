package org.pillarone.riskanalytics.application.environment.applet

import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.environment.applet.application.ULCAppletPane
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.environment.P1RATViewFactory


@CompileStatic
class P1RATAppletViewFactory extends P1RATViewFactory {

    protected ULCRootPane createRootPane() {
        return ULCAppletPane.instance
    }
}