package org.pillarone.riskanalytics.application.ui.base.model
import com.ulcjava.base.application.DefaultComboBoxModel
import groovy.transform.TypeChecked
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure

@TypeChecked
class ModelListModel extends DefaultComboBoxModel {

    private Map modelClasses

    public ModelListModel() {
        modelClasses = [:]
    }

    void load() {
        modelClasses.clear()
        List allModelClasses = ModelStructure.findAllModelClasses()
        removeAllElements()
        allModelClasses.each {Class clazz->
            addElement(clazz.simpleName)
            modelClasses[clazz.simpleName] = clazz
        }
        super.selectedItem = getElementAt(0)
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