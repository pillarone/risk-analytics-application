package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCDialog
import org.pillarone.riskanalytics.application.ui.AbstractDialogTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeHeaderDialogTests extends AbstractDialogTestCase {

    public void testView() {
//        Thread.sleep 15000
    }

    ULCDialog createContentPane() {
        SelectionTreeHeaderDialog headerDialog = new SelectionTreeHeaderDialog(null, 0)
        headerDialog.init()
        headerDialog.dialog.setVisible true
        return headerDialog.dialog;
    }


}
