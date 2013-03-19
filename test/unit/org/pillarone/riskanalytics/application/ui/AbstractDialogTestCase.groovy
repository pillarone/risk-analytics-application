package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCDialog
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractDialogTestCase extends AbstractP1RATTestCase {

    ULCDialog dialog
    ULCDialogOperator dialogOperator

    public void start() {
        LocaleResources.setTestMode()
        dialog = new ULCDialog()
        dialog = createContentPane()
        dialog.setVisible true
    }

    ULCDialogOperator getMainDialogOperator() {
        if (dialogOperator == null) {
            dialogOperator = new ULCDialogOperator(new ComponentByNameChooser("mainDialog"))
        }
        return dialogOperator;
    }

    ULCButtonOperator getButtonOperator(String name) {
        new ULCButtonOperator(getMainDialogOperator(), new ComponentByNameChooser(name))
    }

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainDialogOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainDialogOperator(), new ComponentByNameChooser(name))
    }

    ULCPopupMenuOperator getPopupMenuOperator(String name) {
        return new ULCPopupMenuOperator(getMainDialogOperator(), new ComponentByNameChooser(name))
    }

    abstract ULCDialog createContentPane()


}
