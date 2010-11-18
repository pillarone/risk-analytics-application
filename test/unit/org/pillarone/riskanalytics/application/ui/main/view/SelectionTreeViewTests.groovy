package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
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

    public void testCheckBoxMenu() {
        Thread.sleep 30000
    }



    public void testOpenItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openItem = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openItem
        openItem.clickMouse()
    }

    public void testExportItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem
        exportItem.clickMouse()
    }

    public void testRenameItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
    }

    public void testRun() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator run = new ULCMenuItemOperator(popupMenuOperator, "Run simulation ...")
        assertNotNull run
    }

    public void testSaveAsAction() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator saveAs = new ULCMenuItemOperator(popupMenuOperator, "Save as ...")
        assertNotNull saveAs
        saveAs.clickMouse()
    }

    public void testCreateNewVersion() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        componentTree.selectCell(3, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(3, 0)

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
        openBatch.clickMouse()

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
        componentTree.doExpandRow 1
        componentTree.doExpandRow 4
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(5, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openResult = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openResult
        openResult.clickMouse()

    }

    public void testExportResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 1
        componentTree.doExpandRow 4
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(5, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem

    }

    public void testRenameResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 1
        componentTree.doExpandRow 4
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(5, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
        renameItem.clickMouse()

    }

    ULCComponent createContentPane() {
        viewModel = getMockTreeModel()
        SelectionTreeView view = new SelectionTreeView(getMockP1RATModel(viewModel))
        return view.content;
    }


    private P1RATModel getMockP1RATModel(def viewModel) {
        P1RATModel p1RATModel = new P1RATModel(viewModel)
        p1RATModel.metaClass.startPollingTimer = {PollingBatchSimulationAction pollingBatchSimulationAction ->
        }
        p1RATModel.metaClass.openItem = {Model pcModel, Parameterization item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        p1RATModel.metaClass.openItem = {Model pcModel, Simulation item ->
            assertEquals pcModel.name, "Application"
            assertNotNull item
        }

        return p1RATModel
    }

    private ModellingInformationTableTreeModel getMockTreeModel() {
        ModellingInformationTableTreeModel treeModel = new ModellingInformationTableTreeModel()
        treeModel.metaClass.getAllModelClasses = {->
            return [ApplicationModel]
        }
        treeModel.metaClass.getItemsForModel = {Class modelClass, Class clazz ->
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
                    simulation.setEnd(new Date())
                    return [simulation]
                default: return []
            }
        }
        treeModel.metaClass.getAllBatchRuns = {->
            return [new BatchRun(name: "test")]
        }
        treeModel.buildTreeNodes()
        return treeModel
    }



    Parameterization createStubParameterization(int index, Status status) {
        Parameterization parameterization = new Parameterization("param" + index)
        parameterization.id = index
        Person person = new Person(username: "username" + index)
        parameterization.setCreator(person)
        parameterization.setCreationDate(new Date())
        Person person2 = new Person(username: "modificator" + index)
        parameterization.setLastUpdater(person2)
        parameterization.setModificationDate(new Date())
        parameterization.status = status
        return parameterization

    }

    Simulation createNewSimulation() {
        Simulation simulation = new Simulation("simulation2")
        simulation.modelClass = ApplicationModel
        simulation.parameterization = new Parameterization("param1")
        simulation.template = new ResultConfiguration("result1")
        simulation.id = 2
        simulation.setEnd(new Date())
        return simulation
    }


}
