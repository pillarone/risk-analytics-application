package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.pillarone.riskanalytics.application.ui.base.model.MultiFilteringTableTreeModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.testframework.operator.*
import com.ulcjava.base.application.ULCCheckBox
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import models.application.ApplicationModel
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.user.Person

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractP1RATTestCase extends AbstractSimpleStandaloneTestCase {

    private static Log LOG = LogFactory.getLog(AbstractP1RATTestCase)

    ULCFrame frame
    ULCFrameOperator mainFrameOperator

    public void start() {
        LocaleResources.setTestMode()
        frame = new ULCFrame()
        frame.setTitle("mainFrame")
        frame.setName("mainFrame")
        frame.setSize(1024, 768)
        ULCBoxPane contentPane = new ULCBoxPane()
        frame.contentPane = contentPane
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, createContentPane())
        frame.setVisible true
    }

    @Override
    protected void setUp() {
        try {
            super.setUp()
        } catch (Exception e) {
            LOG.error("Setup failed", e)
            throw e;
        }
    }



    ULCFrameOperator getMainFrameOperator() {
        if (mainFrameOperator == null) {
            mainFrameOperator = new ULCFrameOperator(new ComponentByNameChooser("mainFrame"))
        }
        return mainFrameOperator;
    }

    ULCTableTreeOperator getTableTreeOperatorByName(String name) {
        new ULCTableTreeOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCTextFieldOperator getTextFieldOperator(String name) {
        new ULCTextFieldOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCButtonOperator getButtonOperator(String name) {
        new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCToggleButtonOperator getToggleButtonOperator(String name) {
        new ULCToggleButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCCheckBoxOperator getCheckBoxOperator(String name) {
        new ULCCheckBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCPopupMenuOperator getPopupMenuOperator(String name) {
        return new ULCPopupMenuOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCSpinnerOperator getSpinnerOperator(String name) {
        new ULCSpinnerOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    abstract ULCComponent createContentPane()


    protected RiskAnalyticsMainModel getMockRiskAnalyticsMainModel() {
        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel(getMockTreeModel(null))
        mainModel.metaClass.startPollingTimer = { PollingBatchSimulationAction pollingBatchSimulationAction ->
        }
        mainModel.metaClass.openItem = { Model pcModel, Parameterization item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        mainModel.metaClass.openItem = { Model pcModel, Simulation item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        return mainModel
    }

    private MultiFilteringTableTreeModel getMockTreeModel(RiskAnalyticsMainModel mainModel) {
        ModellingInformationTableTreeModel treeModel = new ModellingInformationTableTreeModel(mainModel)
        treeModel.builder.metaClass.getAllModelClasses = {->
            return [ApplicationModel]
        }
        treeModel.builder.metaClass.getItemsForModel = {Class modelClass, Class clazz ->
            switch (clazz) {
                case Simulation:
                    Simulation simulation = new Simulation("simulation1")
                    simulation.parameterization = new Parameterization("param1")
                    simulation.template = new ResultConfiguration("result1")
                    simulation.id = 1
                    simulation.setEnd(new DateTime())
                    simulation.modelClass = ApplicationModel
                    simulation.metaClass.getSize = {Class SimulationClass -> return 0}
                    return [simulation]
                default: return []
            }
        }

        treeModel.builder.metaClass.getAllBatchRuns = {->
            return [new BatchRun(name: "test")]
        }

        treeModel.service = new ModellingItemSearchService()
        treeModel.service.metaClass.getAllItems = {->
            Parameterization parameterization1 = createStubParameterization(1, Status.NONE)
            Parameterization parameterization2 = createStubParameterization(2, Status.DATA_ENTRY)
            parameterization2.versionNumber = new VersionNumber("R1")
            Parameterization parameterization3 = createStubParameterization(3, Status.IN_REVIEW)
            ResultConfiguration resultConfiguration = new ResultConfiguration("result1")
            resultConfiguration.modelClass = ApplicationModel
            [parameterization1, parameterization2, parameterization3, resultConfiguration]
        }

//        treeModel.metaClass.getValue = {Parameterization p, ParameterizationNode node, int columnIndex ->
//            treeModel.addColumnValue(p, node, columnIndex, p.name + " " + columnIndex)
//            return p.name + " " + columnIndex
//        }
//        treeModel.builder = builder
        treeModel.buildTreeNodes()
        return new MultiFilteringTableTreeModel(treeModel)
    }



    Parameterization createStubParameterization(int index, Status status) {
        Parameterization parameterization = new Parameterization("param" + index, ApplicationModel)
        parameterization.id = index
        Person person = new Person(username: "username" + index)
        parameterization.setCreator(person)
        parameterization.setCreationDate(new DateTime())
        Person person2 = new Person(username: "modificator" + index)
        parameterization.setLastUpdater(person2)
        parameterization.setModificationDate(new DateTime())
        parameterization.status = status
        parameterization.modelClass = ApplicationModel
        parameterization.@loaded = true
        return parameterization

    }

    Simulation createNewSimulation() {
        Simulation simulation = new Simulation("simulation2")
        simulation.modelClass = ApplicationModel
        simulation.parameterization = new Parameterization("param1")
        simulation.template = new ResultConfiguration("result1")
        simulation.id = 2
        simulation.setEnd(new DateTime())
        return simulation
    }

}
