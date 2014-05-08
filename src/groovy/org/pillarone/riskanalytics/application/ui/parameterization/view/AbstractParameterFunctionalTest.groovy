package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.*
import grails.util.Holders
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import java.awt.event.InputEvent

abstract class AbstractParameterFunctionalTest extends AbstractSimpleFunctionalTest {
    ApplicationModel model
    Parameterization parameterization
    String parameterizationName
    int parameterizationVersion
    Long parameterizationId

    protected void doStart() {
        parameterizationName = 'ApplicationParameters'
        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        ModellingItemFactory.clear()
        Holders.grailsApplication.mainContext.cacheItemSearchService.refresh()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        model = new ApplicationModel()
        model.init()

        parameterization = findParameterizationByName(parameterizationName)
        parameterization.load()
        parameterizationVersion = parameterization.dao.version
        parameterizationId = parameterization.dao.id


        ModelStructure structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('ApplicationWithoutHierarchyStructure'))
        structure.load()
        ParameterViewModel viewModel = new ParameterViewModel(model, parameterization,
                structure)
        ParameterView view = new ParameterView(viewModel)

        ModellingUIItem uiItem = new ParameterizationUIItem(parameterization) {
            @Override
            IDetailView createDetailView() {
                return view
            }
        }
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager).createDetailViewForItem(uiItem)
        parameterization.addListener(viewModel)
        frame.setContentPane(view.content)

        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    protected Parameterization findParameterizationByName(String name) {
        ModellingItemFactory.getParameterization(ParameterizationDAO.findByName(name))
    }

    protected Parameterization findParameterizationById() {
        ModellingItemFactory.getParameterization(ParameterizationDAO.findById(parameterizationId))
    }


    protected ULCTableTreeOperator getTree() {
        new ULCTableTreeOperator(frame, new ComponentByNameChooser("parameterTreeRowHeader"))
    }

    protected ULCFrameOperator getFrame() {
        new ULCFrameOperator("test")
    }

    protected duplicateParameter(List parentPath, String paramName, String newName) {
        def path = tree.findPath(parentPath + paramName as String[])
        assert path
        tree.doExpandPath(path)
        int row = tree.getRowForPath(path)
        tree.clickOnCell(row, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(row, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.remove"))
        ULCMenuItemOperator menuItem = new ULCMenuItemOperator(popupMenu, "Duplicate")
        menuItem.clickMouse()

        ULCDialogOperator dialog = new ULCDialogOperator(frame)
        final ULCTextFieldOperator name = new ULCTextFieldOperator(dialog)
        name.enterText(newName)
        def path1 = tree.findPath(parentPath + paramName as String[])
        def path2 = tree.findPath(parentPath + newName as String[])
        assert path1
        assert path2
    }

    protected removeParameter(List pathList) {
        def path = tree.findPath(pathList as String[])
        assert path
        tree.doExpandPath(path)
        int row = tree.getRowForPath(path)
        tree.clickOnCell(row, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(row, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.remove"))
        ULCMenuItemOperator menuItem = new ULCMenuItemOperator(popupMenu, "Remove")
        menuItem.clickMouse()
    }

    protected save() {
        tree.getFocus()
        tree.pressKey(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)
    }
}
