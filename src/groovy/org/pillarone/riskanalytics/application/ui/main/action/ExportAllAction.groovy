package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportAllAction extends ExportAction {

    boolean onlyNewestVersion

    public ExportAllAction(boolean onlyNewestVersion) {
        super(onlyNewestVersion ? "ExportAllParameterizationsNewestVersion" : "ExportAllParameterizations")
        this.onlyNewestVersion = onlyNewestVersion
    }


    public void doActionPerformed(ActionEvent event) {
        List items = []
        ModelStructure.findAllModelClasses().each { Class modelClass ->
            items.addAll((onlyNewestVersion) ? ModellingItemFactory.getNewestParameterizationsForModel(modelClass) : ModellingItemFactory.getParameterizationsForModel(modelClass))
        }
        exportItems(items)
    }

    String getFileName(int itemCount, Object filePaths, ModellingItem item) {
        String paramName = "${getName(item)}_v${item.versionNumber}"
        paramName = paramName.replaceAll(" ", "")
        File file = new File("${filePaths[0]}/${item.modelClass.name}/")
        if (!file.exists())
            file.mkdir()
        return "${filePaths[0]}/${item.modelClass.name}/${paramName}.groovy"
    }

}
