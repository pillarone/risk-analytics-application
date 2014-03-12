package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import grails.test.mixin.TestMixin
import models.core.CoreModel
import org.junit.Test
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.RuntimeParameterCollector
import org.pillarone.riskanalytics.core.SimulationProfileDAO
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

@TestMixin(LoadCoreModelMixin)
class SimulationSettingsPaneModelTests {

    @Test
    void testCreateProfile() {
        mockDomain(SimulationProfileDAO)
        def model = new SimulationSettingsPaneModel(CoreModel)
        assertModelFitsToProfile(model.createTemplate('new template'), model)

        model.numberOfIterations = 123
        model.randomSeed = 1234
        assertModelFitsToProfile(model.createTemplate('new template'), model)
    }

    @Test(expected = IllegalStateException)
    void testSanityCheck() {
        def name = ResultConfigurationDAO.findByModelClassName(CoreModel.name).name
        ResultConfiguration template = new ResultConfiguration(name, CoreModel)
        template.load()
        def profile = new SimulationProfile('name')
        profile.template = template
        def model = new SimulationSettingsPaneModel(CoreModel)
        //this should fail, because the runtimeParameters of this profile does not match the model
        model.applyTemplate(profile)
    }

    private void assertModelFitsToProfile(SimulationProfile profile, SimulationSettingsPaneModel model) {
        assert profile.name == 'new template'
        assert profile.modelClass == CoreModel
        assert profile.numberOfIterations == model.numberOfIterations
        assert profile.randomSeed == model.randomSeed
        List<RuntimeParameterCollector.RuntimeParameterDescriptor> descriptors = model.parameterPaneModel.runtimeParameters
        assert profile.runtimeParameters.size() == descriptors.size()
        descriptors.each {
            def holder = profile.getParameterHolder(it.propertyName, 0)
            assert holder
            assert holder.businessObject == it.value
        }
        assert profile.template == model.resultConfigurationVersions.selectedObject
    }


}
