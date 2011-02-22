package org.pillarone.riskanalytics.application.ui.simulation.model

import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.util.UserPreferences

public class ResultConfigurationNameListModel extends DefaultComboBoxModel {

    protected Class modelClass
    UserPreferences userPreferences

    public ResultConfigurationNameListModel() {
        userPreferences = new UserPreferences()
    }

    void load(Class modelClass) {
        if (this.modelClass != modelClass) {
            loadFromDB(modelClass)
        }
    }

    void reload() {
        loadFromDB(modelClass)
    }

    private def loadFromDB(Class modelClass) {
        removeAllElements()
        this.modelClass = modelClass
        def criteria = ResultConfigurationDAO.createCriteria()
        def params = criteria.list {
            eq('modelClassName', modelClass.name)
            projections {
                distinct("name")
            }
        }
        Object defaultItem = getDefaultResultConfiguration(params, modelClass.name)
        params.each {
            addElement(it)
        }
        selectedItem = defaultItem ? defaultItem : getElementAt(0)
    }

    @Override
    void setSelectedItem(Object o) {
        if (o) {
            userPreferences.setDefaultResult(modelClass.name, o)
            super.setSelectedItem(o)
        }
    }

    Object getDefaultResultConfiguration(def params, String modelClassName) {
        String defaultResultConfiguration = userPreferences.getDefaultResult(modelClassName)
        if (!defaultResultConfiguration) {
            defaultResultConfiguration = modelClass.newInstance().getDefaultResultConfiguration()
        }
        return params?.contains(defaultResultConfiguration) ? defaultResultConfiguration : null
    }

    void setDefaultItem(String modelClassName, String defaultResult) {
        userPreferences.setDefaultResult(modelClassName, defaultResult)
    }


}
