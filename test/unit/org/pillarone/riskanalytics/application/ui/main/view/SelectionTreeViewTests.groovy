package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import models.application.ApplicationModel
import models.core.CoreModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.P1UnitTestMixin
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.workflow.Status

import javax.swing.tree.TreePath

@TestMixin(GrailsUnitTestMixin)
@Mixin(P1UnitTestMixin)
class SelectionTreeViewTests extends AbstractSimpleStandaloneTestCase {

    @Override
    void start() {
        LocaleResources.testMode = true
        inTestFrame(createContentPane())
    }

    ULCComponent createContentPane() {
        NavigationTableTreeBuilder builder = new NavigationTableTreeBuilder()
        builder.metaClass.getAllModelClasses = { -> [ApplicationModel] }
        builder.metaClass.getAllBatchRuns = { -> [new BatchRun(name: "test")] }
        def tableTreeModel = new TestNavigationTableTreeModel(navigationTableTreeBuilder: builder)
        tableTreeModel.initialize()
        def view = new SelectionTreeView(riskAnalyticsEventBus: new RiskAnalyticsEventBus(), navigationTableTreeModel: tableTreeModel)
        view.initialize()
        view.content;
    }

    @Override
    protected void setUp() throws Exception {
        initGrailsApplication()
        defineBeans {
            riskAnalyticsEventBus(RiskAnalyticsEventBus)
            detailViewManager(DetailViewManager)
        }
        super.setUp()
    }

    @Override
    void tearDown() {
        super.tearDown()
        LocaleResources.testMode = false
        PopupMenuRegistry.clear()
    }

    void testOpenItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openItem = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openItem
    }

    void testExportItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem
        exportItem.clickMouse()
    }

    void testRenameItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
    }

    void testRun() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

    }

    void testSaveAsAction() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator saveAs = new ULCMenuItemOperator(popupMenuOperator, "Save as ...")
        assertNotNull saveAs
        saveAs.clickMouse()
    }

    void testCreateNewVersion() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator newVersion = new ULCMenuItemOperator(popupMenuOperator, "Create new version")
        assertNotNull newVersion
    }

    public void testOpenResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openResult = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openResult
    }

    public void testExportResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem

    }

    public void testRenameResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
        renameItem.clickMouse()

    }

    public void testCheckForAdditionalMenuItems() {
        PopupMenuRegistry.register(ParameterizationNode, new TestMenuItemCreator(name: 'ParamItem'))
        PopupMenuRegistry.register(ItemGroupNode, Parameterization, new TestMenuItemCreator(name: 'ParamGroup'))
        PopupMenuRegistry.register(ItemGroupNode, Simulation, new TestMenuItemCreator(name: 'SimGroup'))
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        ULCPopupMenuOperator popup = componentTree.callPopupOnCell(2, 0)
        assertNotNull popup
        ULCMenuItemOperator paramItem = new ULCMenuItemOperator(popup, "ParamItem")
        assert paramItem
        popup = componentTree.callPopupOnCell(1, 0)
        ULCMenuItemOperator paramGroup = new ULCMenuItemOperator(popup, "ParamGroup")
        assert paramGroup
        TreePath simulationPath = componentTree.findPath(['Application', 'Results'] as String[])
        componentTree.doExpandPath(simulationPath)
        popup = componentTree.callPopupOnCell(componentTree.getRowForPath(simulationPath), 0)
        ULCMenuItemOperator simGroup = new ULCMenuItemOperator(popup, "SimGroup")
        assert simGroup
    }

    class TestMenuItemCreator implements ULCTableTreeMenuItemCreator {
        String name

        @Override
        ULCMenuItem createComponent(ULCTableTree tree) {
            return new ULCMenuItem(name)
        }
    }

    static class TestNavigationTableTreeModel extends NavigationTableTreeModel {

        @Override
        List getPendingEvents() {
            []
        }

        @Override
        List<ModellingItem> getFilteredItems() {
            Parameterization parameterization1 = createStubParameterization(1, Status.NONE)
            Parameterization parameterization2 = createStubParameterization(2, Status.DATA_ENTRY)
            parameterization2.versionNumber = new VersionNumber("R1")
            Parameterization parameterization3 = createStubParameterization(3, Status.IN_REVIEW)
            ResultConfiguration resultConfiguration = new ResultConfiguration("result1", ApplicationModel)
            Simulation simulation = new Simulation("simulation1")
            simulation.parameterization = new Parameterization("param1")
            simulation.parameterization.modelClass = CoreModel
            simulation.template = new ResultConfiguration("result1", CoreModel)
            simulation.id = 1
            simulation.start = new DateTime()
            simulation.end = new DateTime()
            simulation.modelClass = ApplicationModel
            simulation.metaClass.getSize = { Class SimulationClass -> 0 }
            [parameterization1, parameterization2, parameterization3, resultConfiguration, simulation]
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
}
