package org.pillarone.riskanalytics.application.ui.main.view.item

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 *
 */
class CompareParameterizationUIItemTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization1
    Parameterization parameterization2

    @Override protected void doStart() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])

        Model model = new CoreModel()
        model.init()

        parameterization1 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization1.load()

        parameterization2 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreAlternativeParameters'))
        parameterization2.load()

        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        CompareParameterizationUIItem uiItem = new CompareParameterizationUIItem(mainModel, model, [parameterization1, parameterization2])

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        frame.setContentPane(uiItem.createDetailView())
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true

    }

    public void testView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
    }


}
