package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.ULCFrame
import java.awt.event.InputEvent
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.testframework.operator.*

class AddRemoveDynamicComponentTests extends AbstractSimpleFunctionalTest {

    ApplicationModel model

    protected void doStart() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        model = new ApplicationModel()
        model.init()

        Parameterization parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        parameterization.load()

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        frame.setContentPane(new ParameterizationUIItem(mainModel, model, parameterization).createDetailView())

        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    void testAddRemove() {
        ULCFrameOperator frame = new ULCFrameOperator("test")

        ULCTableTreeOperator tree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("parameterTreeRowHeader"))

        def dynamicNode = tree.getChild(tree.getRoot(), 2)
        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())

        tree.clickOnCell(3, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(3, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator menuItem = new ULCMenuItemOperator(popupMenu, "Add")
        menuItem.clickMouse()

        ULCDialogOperator dialog = new ULCDialogOperator(frame)
        final ULCTextFieldOperator name = new ULCTextFieldOperator(dialog)
        name.enterText("new component")

        assertEquals(2, tree.getChildCount(dynamicNode))
        assertEquals(2, model.dynamicComponent.componentList.size())
        assertEquals("New Component", tree.getValueAt(5, 0))

        tree.clickOnCell(5, 0, 1, InputEvent.BUTTON1_MASK)
        tree.clickOnCell(5, 0, 1, InputEvent.BUTTON3_MASK)

        popupMenu = new ULCPopupMenuOperator(frame, new ComponentByNameChooser("popup.remove"))
        menuItem = new ULCMenuItemOperator(popupMenu, "Remove")
        menuItem.clickMouse()

        assertEquals(1, tree.getChildCount(dynamicNode))
        assertEquals(1, model.dynamicComponent.componentList.size())
    }


}
