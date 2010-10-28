package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportAllAction extends ExportAction {

    P1RATMainView p1RATMainView
    boolean onlyNewestVersion


    public ExportAllAction(P1RATMainView p1RATMainView, P1RATModel model, boolean onlyNewestVersion) {
        super(onlyNewestVersion ? "ExportAllParameterizationsNewestVersion" : "ExportAllParameterizations")
        this.p1RATMainView = p1RATMainView
        this.model = model
        this.onlyNewestVersion = onlyNewestVersion
    }


    public void doActionPerformed(ActionEvent event) {
        List items = []
        ModelStructure.findAllModelClasses().each {Class modelClass ->
            items.addAll((onlyNewestVersion) ? ModellingItemFactory.getNewestParameterizationsForModel(modelClass) : ModellingItemFactory.getParameterizationsForModel(modelClass))
        }
        exportItems(items)
    }

    String getFileName(int itemCount, Object filePaths, ModellingItem item) {
        String paramName = "${item.name}_v${item.versionNumber}"
        paramName = paramName.replaceAll(" ", "")
        File file = new File("${filePaths[0]}/${item.modelClass.name}/")
        if (!file.exists())
            file.mkdir()
        return "${filePaths[0]}/${item.modelClass.name}/${paramName}.groovy"
    }

    ULCWindow getAncestor() {
        ULCWindow ancestor = UlcUtilities.getWindowAncestor(p1RATMainView.content)
        return ancestor
    }


}
