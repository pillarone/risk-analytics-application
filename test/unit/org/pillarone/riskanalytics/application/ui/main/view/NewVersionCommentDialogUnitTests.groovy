package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewVersionCommentDialogUnitTests extends AbstractP1RATTestCase {

    public void testView(){
//        Thread.sleep(2000)
    }

    @Override
    ULCComponent createContentPane() {
        Parameterization parameterization = new Parameterization("param")
        ParameterizationUIItem uiItem = UIItemFactory.createItem(parameterization, new CoreModel(), null)
        Closure ok = {e ->}
        NewVersionCommentDialog dialog = new NewVersionCommentDialog(ok)
        return dialog.dialog.getContentPane()
    }


}
