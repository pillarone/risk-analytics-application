package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewVersionCommentDialogUnitTests extends AbstractP1RATTestCase {

    public void testView(){
//        Thread.sleep(2000)
    }

    @Override
    ULCComponent createContentPane() {
        Closure ok = {e ->}
        NewVersionCommentDialog dialog = new NewVersionCommentDialog(ok)
        return dialog.dialog.contentPane
    }


}
