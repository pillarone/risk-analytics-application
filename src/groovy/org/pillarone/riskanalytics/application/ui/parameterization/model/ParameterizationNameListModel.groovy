package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.ParameterizationDAO

public class ParameterizationNameListModel extends DefaultComboBoxModel {

    protected Class modelClass


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
        def criteria = ParameterizationDAO.createCriteria()
        def params = criteria.list {
            eq('modelClassName', modelClass.name)
            eq('valid',true)
            projections {
                distinct("name")
            }
        }

        params.each {
            addElement(it)
        }
        selectedItem = getElementAt(0)
    }
}