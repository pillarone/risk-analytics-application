package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure

class ModelListModel extends DefaultComboBoxModel {

    private Map modelClasses
    private List listDataListener

    public ModelListModel() {
        modelClasses = [:]
    }

    void load() {
        modelClasses.clear()
        List allModelClasses = ModelStructure.findAllModelClasses()
        removeAllElements()
        allModelClasses.each {
            addElement(it.simpleName)
            modelClasses[it.simpleName] = it
        }
        super.setSelectedItem(getElementAt(0))
    }

    void reload() {
        load()
    }

    Object getSelectedObject() {
        return modelClasses[getSelectedItem()]
    }

    void setSelectedObject(Class modelClass) {
        setSelectedItem(modelClass.simpleName)
    }
}