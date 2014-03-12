package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before
import org.pillarone.riskanalytics.core.*
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.*
import org.pillarone.riskanalytics.core.parameter.*
import org.pillarone.riskanalytics.core.user.Person

class LoadCoreModelMixin extends DomainClassUnitTestMixin {

    @Before

    void loadCoreModel() {
        mockDomain(ModelStructureDAO)
        mockDomain(ModelDAO)
        mockDomain(ParameterizationDAO)
        mockDomain(ResultConfigurationDAO)
        mockDomain(ConfigObjectHolder)
        mockDomain(Parameter)
        mockDomain(ConstrainedStringParameter)
        mockDomain(DateParameter)
        mockDomain(MultiDimensionalParameter)
        mockDomain(IntegerParameter)
        mockDomain(DoubleParameter)
        mockDomain(BooleanParameter)
        mockDomain(ResourceParameter)
        mockDomain(EnumParameter)
        mockDomain(StringParameter)
        mockDomain(ParameterObjectParameter)
        mockDomain(MultiDimensionalParameterTitle)
        mockDomain(MultiDimensionalParameterValue)
        mockDomain(ParameterizationDAO)
        mockDomain(SimulationTag)
        mockDomain(ParameterizationTag)
        mockDomain(ParameterEntry)
        mockDomain(ParameterObjectParameter)
        mockDomain(MultiDimensionalParameterValue)
        mockDomain(SimulationRun)
        mockDomain(ResourceDAO)
        mockDomain(SimulationProfileDAO)
        mockDomain(Person)
        mockDomain(PathMapping)
        mockDomain(CollectorInformation)
        mainContext.registerSingleton('springSecurityService', SpringSecurityServiceMock)
        FileImportService.importModelsIfNeeded(['Core'])
    }

    static class SpringSecurityServiceMock extends SpringSecurityService {

        @Override
        Object getCurrentUser() {
            null
        }
    }
}
