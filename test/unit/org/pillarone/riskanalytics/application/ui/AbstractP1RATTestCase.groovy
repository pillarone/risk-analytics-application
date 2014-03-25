package org.pillarone.riskanalytics.application.ui
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.*
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import models.application.ApplicationModel
import models.core.CoreModel
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.search.CacheItemEventConsumer
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.workflow.Status
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractP1RATTestCase extends AbstractSimpleStandaloneTestCase {

    private static final Log LOG = LogFactory.getLog(AbstractP1RATTestCase)

    ULCFrame frame
    ULCFrameOperator mainFrameOperator

    public void start() {
        LocaleResources.testMode = true
        frame = new ULCFrame()
        frame.title = "mainFrame"
        frame.name = "mainFrame"
        frame.setSize(1024, 768)
        ULCBoxPane contentPane = new ULCBoxPane()
        frame.contentPane = contentPane
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, createContentPane())
        frame.visible = true
    }

    @Override
    protected void setUp() {
        try {
            ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
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

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCCheckBoxOperator getCheckBoxOperator(String name) {
        new ULCCheckBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    abstract ULCComponent createContentPane()

    protected RiskAnalyticsMainModel getMockRiskAnalyticsMainModel() {
        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()

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

    protected ModellingInformationTableTreeModel getMockTreeModel(RiskAnalyticsMainModel mainModel) {
        ModellingInformationTableTreeModel treeModel = new ModellingInformationTableTreeModel(riskAnalyticsMainModel: mainModel)
        treeModel.builder = new ModellingInformationTableTreeBuilder(treeModel, mainModel)
        treeModel.builder.metaClass.getAllModelClasses = { ->
            [ApplicationModel]
        }

        treeModel.builder.metaClass.getAllBatchRuns = { ->
            [new BatchRun(name: "test")]
        }
        treeModel.metaClass.getPendingEvents = { CacheItemEventConsumer consumer ->
            []
        }
        treeModel.metaClass.getFilteredItems = { ->
            Parameterization parameterization1 = createStubParameterization(1, Status.NONE)
            Parameterization parameterization2 = createStubParameterization(2, Status.DATA_ENTRY)
            parameterization2.versionNumber = new VersionNumber("R1")
            Parameterization parameterization3 = createStubParameterization(3, Status.IN_REVIEW)
            ResultConfiguration resultConfiguration = new ResultConfiguration("result1")
            resultConfiguration.modelClass = ApplicationModel
            Simulation simulation = new Simulation("simulation1")
            simulation.parameterization = new Parameterization("param1")
            simulation.parameterization.modelClass = CoreModel
            simulation.template = new ResultConfiguration("result1")
            simulation.template.modelClass = CoreModel
            simulation.id = 1
            simulation.end = new DateTime()
            simulation.modelClass = ApplicationModel
            simulation.metaClass.getSize = { Class SimulationClass -> 0 }
            [parameterization1, parameterization2, parameterization3, resultConfiguration, simulation]
        }

        treeModel.buildTreeNodes()
        return treeModel
    }


    Parameterization createStubParameterization(int index, Status status) {
        Parameterization parameterization = new Parameterization("param" + index, ApplicationModel)
        parameterization.id = index
        Person person = new Person(username: "username" + index)
        parameterization.creator = person
        parameterization.creationDate = new DateTime()
        Person person2 = new Person(username: "modificator" + index)
        parameterization.lastUpdater = person2
        parameterization.modificationDate = new DateTime()
        parameterization.status = status
        parameterization.modelClass = ApplicationModel
        parameterization.loaded = true
        return parameterization

    }
}
