package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportAllAction extends ExportAction {

    RiskAnalyticsMainView mainView
    boolean onlyNewestVersion


    public ExportAllAction(RiskAnalyticsMainModel model, boolean onlyNewestVersion) {
        super(onlyNewestVersion ? "ExportAllParameterizationsNewestVersion" : "ExportAllParameterizations")
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
//        ULCWindow ancestor = UlcUtilities.getWindowAncestor(mainView.content)
        //        return ancestor
        //todo fja
    }


}
