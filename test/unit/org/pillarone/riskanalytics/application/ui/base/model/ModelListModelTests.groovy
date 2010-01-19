package org.pillarone.riskanalytics.application.ui.base.model

import groovy.mock.interceptor.StubFor
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure

class ModelListModelTests extends GroovyTestCase {

    void testLoad() {
        ModelListModel listModel = new ModelListModel()
        assertEquals 0, listModel.size

        StubFor modelStructureStub = new StubFor(ModelStructure)
        modelStructureStub.demand.findAllModelClasses {-> [CoreModel, ApplicationModel]}

        modelStructureStub.use {
            listModel.load()
            assertEquals 2, listModel.size
        }
    }

    void testGetElementAt() {
        ModelListModel listModel = new ModelListModel()
        assertEquals 0, listModel.size

        StubFor modelStructureStub = new StubFor(ModelStructure)
        modelStructureStub.demand.findAllModelClasses {-> [CoreModel, ApplicationModel]}

        modelStructureStub.use {
            listModel.load()
            assertEquals ApplicationModel.simpleName, listModel.getElementAt(1)
        }
    }

    void testGetSelectedItem() {
        ModelListModel listModel = new ModelListModel()
        assertEquals 0, listModel.size

        StubFor modelStructureStub = new StubFor(ModelStructure)
        modelStructureStub.demand.findAllModelClasses {-> [CoreModel, ApplicationModel]}

        modelStructureStub.use {
            listModel.load()
            listModel.setSelectedItem(listModel.getElementAt(1))
            assertEquals ApplicationModel.simpleName, listModel.getSelectedItem()
        }
    }

    void testGetSelectedObject() {
        ModelListModel listModel = new ModelListModel()
        assertEquals 0, listModel.size

        StubFor modelStructureStub = new StubFor(ModelStructure)
        modelStructureStub.demand.findAllModelClasses {-> [CoreModel, ApplicationModel]}

        modelStructureStub.use {
            listModel.load()
            listModel.setSelectedItem(listModel.getElementAt(1))
            assertEquals ApplicationModel, listModel.getSelectedObject()
        }
    }
}