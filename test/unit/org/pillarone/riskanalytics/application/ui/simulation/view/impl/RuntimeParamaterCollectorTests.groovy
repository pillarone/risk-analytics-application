package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.RuntimeParameterCollector.RuntimeParameterDescriptor

class RuntimeParamaterCollectorTests extends GroovyTestCase {

    void testVisitor() {
        ApplicationModel model = new ApplicationModel()
        model.init()

        RuntimeParameterCollector collector = new RuntimeParameterCollector()
        model.accept(collector)


        final Set<RuntimeParameterDescriptor> runtimeParameters = collector.runtimeParameters
        assertEquals 7, runtimeParameters.size()

        final RuntimeParameterDescriptor dateParameter = runtimeParameters.find { it.typeClass == DateTime }
        assertEquals "runtimeDateParameter", dateParameter.propertyName
        assertNotNull dateParameter.value

        final RuntimeParameterDescriptor stringParameter = runtimeParameters.find { it.typeClass == String }
        assertEquals "runtimeStringParameter", stringParameter.propertyName
        assertNotNull stringParameter.value
    }
}
