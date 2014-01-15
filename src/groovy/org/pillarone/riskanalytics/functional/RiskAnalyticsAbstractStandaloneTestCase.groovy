package org.pillarone.riskanalytics.functional

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.util.prefs.impl.MockUserPreferences
import org.pillarone.riskanalytics.core.*
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.workflow.AuditLog

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public abstract class RiskAnalyticsAbstractStandaloneTestCase extends AbstractStandaloneTestCase {

    private static Log LOG = LogFactory.getLog(RiskAnalyticsAbstractStandaloneTestCase)

    protected void setUp() throws Exception {
        handleConfiguration()
        try {
            super.setUp()
        } catch (Exception e) {
            LOG.error("Setup failed", e)
            throw e;
        }
    }

    protected void tearDown() {
        MockUserPreferences.INSTANCE.clearFakePreferences()
        ModelRegistry.instance.listeners.clear() //TODO: find better solution
        Thread cleanUpThread = new Thread(
                [run: {
                    SimulationRun.withTransaction {
                        AuditLog.list()*.delete()
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

    protected printTree(ULCTableTree tree) {
        printNode(tree.getModel().root, 0)
    }

    private printNode(ITableTreeNode node, int level) {
        String line = ""
        level.times { line += "\t" }
        line += "${node.getValueAt(0)},"
        println line
        level++
        node.childCount.times {
            printNode(node.getChildAt(it), level)
        }
    }

}
