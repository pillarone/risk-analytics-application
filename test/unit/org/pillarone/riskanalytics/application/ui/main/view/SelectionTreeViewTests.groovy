package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.MultiFilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.workflow.Status

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeViewTests extends AbstractP1RATTestCase {
    ModellingInformationTableTreeModel viewModel


    ULCComponent createContentPane() {
        RiskAnalyticsMainModel mainModel = getMockRiskAnalyticsMainModel()
        SelectionTreeView view = new SelectionTreeView(mainModel)
        return view.content;
    }

    public void testView() {
//        Thread.sleep 15000
    }



    public void testOpenItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openItem = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openItem
    }

    public void testExportItem() {

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

    public void testRenameItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
    }

    public void testRun() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

    }

    public void testSaveAsAction() {
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

    public void testCreateNewVersion() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator newVersion = new ULCMenuItemOperator(popupMenuOperator, "Create new version")
        assertNotNull newVersion
    }


    public void testOpenBatch() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 2
        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openBatch

    }

    public void testNewBatch() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 2
        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "New")
        assertNotNull openBatch

    }

    public void testRunBatch() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 2
        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Run now")
        assertNotNull openBatch

    }

    public void testDeleteBatch() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 2
        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Delete")
        assertNotNull openBatch

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



    private RiskAnalyticsMainModel getMockRiskAnalyticsMainModel() {
        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel(getMockTreeModel(null))
        mainModel.metaClass.startPollingTimer = {PollingBatchSimulationAction pollingBatchSimulationAction ->
        }
        mainModel.metaClass.openItem = {Model pcModel, Parameterization item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        mainModel.metaClass.openItem = {Model pcModel, Simulation item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        return mainModel
    }

    private ModellingInformationTableTreeModel getMockTreeModel(RiskAnalyticsMainModel mainModel) {
        ModellingInformationTableTreeModel treeModel = new ModellingInformationTableTreeModel(mainModel)
        treeModel.builder.metaClass.getAllModelClasses = {->
            return [ApplicationModel]
        }
        treeModel.builder.metaClass.getItemsForModel = {Class modelClass, Class clazz ->
            switch (clazz) {
                case Parameterization:
                    Parameterization parameterization1 = createStubParameterization(1, Status.NONE)
                    Parameterization parameterization2 = createStubParameterization(2, Status.DATA_ENTRY)
                    parameterization2.versionNumber = new VersionNumber("R1")
                    Parameterization parameterization3 = createStubParameterization(3, Status.IN_REVIEW)
                    return [parameterization1, parameterization2, parameterization3]
                case ResultConfiguration: return [new ResultConfiguration("result1")]
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

        treeModel.metaClass.getValue = {Parameterization p, ParameterizationNode node, int columnIndex ->
            treeModel.addColumnValue(p, node, columnIndex, p.name + " " + columnIndex)
            return p.name + " " + columnIndex
        }
//        treeModel.builder = builder
        treeModel.buildTreeNodes()
        return treeModel
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
