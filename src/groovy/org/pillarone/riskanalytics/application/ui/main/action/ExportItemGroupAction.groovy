package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportItemGroupAction extends ExportAction {
    boolean onlyNewestVersion = true

    public ExportItemGroupAction(ULCTableTree tree, String title) {
        super(tree, title)
    }

    public ExportItemGroupAction(ULCTableTree tree, String title, boolean onlyNewestVersion) {
        super(tree, onlyNewestVersion ? (title + "NV") : title)
        this.onlyNewestVersion = onlyNewestVersion
    }


    public void doActionPerformed(ActionEvent actionEvent) {
        Class itemClass = getSelectedItemGroupClass()
        def modelClass = getSelectedModel().class
        switch (itemClass) {
            case Parameterization:
                if (onlyNewestVersion)
                    exportItems(ModellingItemFactory.getNewestParameterizationsForModel(modelClass))
                else
                    exportItems(ModellingItemFactory.getParameterizationsForModel(modelClass))
                break;
            case ResultConfiguration:
                exportItems(ModellingItemFactory.getNewestResultConfigurationsForModel(modelClass))
                break;
            case Simulation:
                exportSimulations(ModellingItemFactory.getActiveSimulationsForModel(modelClass))
                break;
        }
    }

    String getFileName(int itemCount, Object filePaths, ModellingItem item) {
        String sep = getFileSeparator()
        String paramName = (item.properties.keySet().contains("versionNumber")) ? "${getName(item)}_v${item.versionNumber}" : "${getName(item)}"
        paramName = paramName.replaceAll(" ", "")
        def index = filePaths[0].indexOf("${item.name}.groovy")
        String dirName = (index != -1) ? new File(filePaths[0]).parent : filePaths[0]
        File file = new File("${dirName + sep + item.modelClass.name + sep}")
        if (!file.exists())
            file.mkdir()
        return validateFileName("${dirName + sep + item.modelClass.name + sep + paramName}.groovy")
    }

}
