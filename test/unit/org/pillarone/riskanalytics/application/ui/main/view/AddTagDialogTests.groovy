package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialogTests extends AbstractStandaloneTestCase {

    ULCDialog dialog



    public void start() {
        dialog = createDialog()
        dialog.setVisible true
    }

    public void testView() {
//        Thread.sleep 10000
    }

    ULCComponent createDialog() {
        AddTagDialog dialog = new AddTagDialog(null, null, null)
        dialog.metaClass.getItems = {->
            return [new Tag(name: "TEST", tagType: EnumTagType.PARAMETERIZATION)]
        }

        dialog.init()
        return dialog.dialog;
    }


}
