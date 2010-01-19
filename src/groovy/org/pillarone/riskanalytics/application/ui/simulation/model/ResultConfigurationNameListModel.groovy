package org.pillarone.riskanalytics.application.ui.simulation.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO

public class ResultConfigurationNameListModel extends DefaultComboBoxModel {

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
        def criteria = ResultConfigurationDAO.createCriteria()
        def params = criteria.list {
            eq('modelClassName', modelClass.name)
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
