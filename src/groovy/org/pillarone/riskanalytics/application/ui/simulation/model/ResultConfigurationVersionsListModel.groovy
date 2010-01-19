package org.pillarone.riskanalytics.application.ui.simulation.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory

public class ResultConfigurationVersionsListModel extends DefaultComboBoxModel {

    protected Class modelClass
    protected String configurationName
    private Map templateObjects = [:]



    void load(Class modelClass, String name) {
        if (this.modelClass != modelClass || configurationName != name) {
            loadFromDB(modelClass, name)
        }
    }

    void reload() {
        loadFromDB(modelClass, configurationName)
    }

    void reload(String name) {
        loadFromDB(modelClass, name)
    }

    private def loadFromDB(Class modelClass, String name) {
        removeAllElements()
        templateObjects.clear()
        this.modelClass = modelClass
        this.configurationName = name
        List allConfigurations = ModellingItemFactory.getResultConfigurationsForModel(modelClass)
        List templates = []
        allConfigurations.each {
            if (it.name.equals(configurationName)) {
                templates.add(it)
            }
        }
        if (templates.size() > 0) {
            templates = templates.sort {it.versionNumber}.reverse()
            addElement getText("NewestVersion")
            templateObjects[getText("NewestVersion")] = templates.get(0)
            templates.remove(templates.get(0))
            templates.each {
                String paramName = "v${it.versionNumber.toString()}"
                addElement paramName
                templateObjects[paramName] = it
            }
            selectedItem = getElementAt(0)
        }
    }


    Object getSelectedObject() {
        return templateObjects[getSelectedItem()]
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ResultTemplateVersionsList." + key);
    }

}
