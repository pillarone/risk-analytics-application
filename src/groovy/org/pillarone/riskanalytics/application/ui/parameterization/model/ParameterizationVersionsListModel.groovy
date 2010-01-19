package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory

public class ParameterizationVersionsListModel extends DefaultComboBoxModel {

    protected Class modelClass
    protected String parameterizationName
    private Map parameterizationObjects = [:]



    void load(Class modelClass, String name) {
        if (this.modelClass != modelClass || parameterizationName != name) {
            loadFromDB(modelClass, name)
        }
    }

    void reload() {
        loadFromDB(modelClass, parameterizationName)
    }

    void reload(String name) {
        loadFromDB(modelClass, name)
    }

    private void loadFromDB(Class modelClass, String name) {
        removeAllElements()
        parameterizationObjects.clear()
        this.modelClass = modelClass
        this.parameterizationName = name
        List allParameterizations = ModellingItemFactory.getParameterizationsForModel(modelClass)
        List parameterizations = []
        allParameterizations.each {
            if (it.valid && it.name.equals(parameterizationName)) {
                parameterizations.add(it)
            }
        }
        if (parameterizations.size() > 0) {
            parameterizations = parameterizations.sort {it.versionNumber}.reverse()
            addElement getText('NewestVersion')
            parameterizationObjects[getText('NewestVersion')] = parameterizations.get(0)
            parameterizations.remove(parameterizations.get(0))
            parameterizations.each {
                String paramName = "v${it.versionNumber.toString()}"
                addElement paramName
                parameterizationObjects[paramName] = it
            }
            selectedItem = getElementAt(0)
        }
    }


    Object getSelectedObject() {
        return parameterizationObjects[getSelectedItem()]
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ParameterizationVersionsList." + key);
    }

}