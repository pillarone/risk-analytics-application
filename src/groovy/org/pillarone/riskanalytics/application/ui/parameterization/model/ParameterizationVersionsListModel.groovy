package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

public class ParameterizationVersionsListModel extends DefaultComboBoxModel {

    private static Log LOG = LogFactory.getLog(ParameterizationVersionsListModel)

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
            if (it.name.equals(parameterizationName)) {
                parameterizations.add(it)
            }
        }
        if (parameterizations.size() > 0) {
            parameterizations = parameterizations.sort {it.versionNumber}.reverse()
            parameterizations.each {
                String paramName = "v${it.versionNumber.toString()}"
                parameterizationObjects[paramName] = it
                addElement paramName
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

    boolean isValid(String parameterizationName) {
        Parameterization parameterization = parameterizationObjects[parameterizationName]
        LOG.debug "Name: $parameterizationName All: ${parameterizationObjects.keySet()}"
        return parameterization?.valid
    }

}