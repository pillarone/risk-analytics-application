package org.pillarone.riskanalytics.functional;


import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.application.util.prefs.impl.MockUserPreferences

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public abstract class RiskAnalyticsAbstractStandaloneTestCase extends AbstractStandaloneTestCase {


    protected void setUp() throws Exception {
        handleConfiguration()
        super.setUp()
    }

    protected void tearDown() {
        MockUserPreferences.INSTANCE.clearFakePreferences()
        ModelRegistry.instance.listeners.clear() //TODO: find better solution
        Thread cleanUpThread = new Thread(
                [run: {
                    SimulationRun.withTransaction {
                        BatchRunSimulationRun.list()*.delete()
                        BatchRun.list()*.delete()
                        PostSimulationCalculation.list()*.delete()
                        SingleValueResult.list()*.delete()
                        SimulationRun.list()*.delete()
                        ResultStructureDAO.list()*.delete()
                        ResultConfigurationDAO.list()*.delete()
                        ParameterizationDAO.list()*.delete()
                        ModelStructureDAO.list()*.delete()
                        ModelDAO.list()*.delete()
                    }
                }] as Runnable
        )
        cleanUpThread.start()
        cleanUpThread.join()
        super.tearDown()
    }


}
