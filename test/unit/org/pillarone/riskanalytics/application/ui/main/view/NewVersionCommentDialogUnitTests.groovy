package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

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
        Closure ok = {e ->}
        NewVersionCommentDialog dialog = new NewVersionCommentDialog(null, parameterization, ok)
        return dialog.dialog.getContentPane()
    }


}
