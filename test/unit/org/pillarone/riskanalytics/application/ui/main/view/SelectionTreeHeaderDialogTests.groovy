package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCDialog
import org.pillarone.riskanalytics.application.ui.AbstractDialogTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeHeaderDialogTests extends AbstractDialogTestCase {

    public void testView() {
//        Thread.sleep 5000
    }

    ULCDialog createContentPane() {
        SelectionTreeHeaderDialog headerDialog = new SelectionTreeHeaderDialog(null, 0)
        headerDialog.metaClass.initFilter = {->
            headerDialog.filterCheckBoxes = []
            headerDialog.filterCheckBoxes << new ULCCheckBox("All")
            for (int i = 0; i < 10; i++) {
                headerDialog.filterCheckBoxes << new ULCCheckBox("filter " + i)
            }
        }

        headerDialog.init()
        headerDialog.dialog.setVisible true
        return headerDialog.dialog;
    }
}
