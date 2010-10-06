package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportItemAction extends ExportAction {

    public ExportItemAction(ULCTree tree, P1RATModel model) {
        super(tree, model, "Export")
    }


    public void doActionPerformed(ActionEvent event) {
        doAction(getSelectedItem())
    }

    protected void doAction(ModellingItem item) {
        if (item.changed) {
            ULCAlert alert = new I18NAlert("UnsavedExport")

            alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                def value = windowEvent.source.value
                if (value.equals(alert.firstButtonLabel)) {
                    item.save()
                    exportItems([item])
                }
            }] as IWindowListener)

            alert.show()
        } else {
            exportItems([item])
        }
    }

    protected void doAction(Simulation item) {
        exportSimulations([item])
    }

}
