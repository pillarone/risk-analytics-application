package org.pillarone.riskanalytics.application

import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.core.output.SimulationRun

abstract class AbstractSimpleFunctionalTest extends AbstractSimpleStandaloneTestCase {

    private Throwable throwable


    final void start() {
        try {
            Person.withTransaction {
                doStart()
            }
        } catch (Throwable t) {
            throwable = t
        }
    }

    abstract protected void doStart()

    void testInitialization() {
        assertNull "Error during doStart(): ${throwable?.message}", throwable
    }

    protected void tearDown() {
        Thread cleanUpThread = new Thread(
                [run: {
                    SimulationRun.list()*.delete()
                    ResultStructureDAO.list()*.delete()
                    ResultConfigurationDAO.list()*.delete()
                    ParameterizationDAO.list()*.delete()
                    ModelStructureDAO.list()*.delete()
                }] as Runnable
        )
        cleanUpThread.start()
        cleanUpThread.join()
        super.tearDown()

    }

}