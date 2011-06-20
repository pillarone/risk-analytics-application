package org.pillarone.riskanalytics.application.ui.util

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.workflow.Status
import org.joda.time.DateTime

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MockObjectFactory {

    public static ModellingInformationTableTreeModel getMockTreeModel() {
        ModellingInformationTableTreeModel treeModel = new ModellingInformationTableTreeModel()
        treeModel.metaClass.getAllModelClasses = {->
            return [ApplicationModel]
        }
        treeModel.metaClass.getItemsForModel = {Class modelClass, Class clazz ->
            switch (clazz) {
                case Parameterization:
                    Parameterization parameterization1 = createStubParameterization(1, Status.NONE)
                    Parameterization parameterization2 = createStubParameterization(2, Status.DATA_ENTRY)
                    parameterization2.versionNumber = new VersionNumber("R1")
                    Parameterization parameterization3 = createStubParameterization(3, Status.IN_REVIEW)
                    return [parameterization1, parameterization2, parameterization3]
                case ResultConfiguration: return [new ResultConfiguration("result1")]
                case Simulation:
                    Simulation simulation = new Simulation("simulation1")
                    simulation.parameterization = new Parameterization("param1")
                    simulation.template = new ResultConfiguration("result1")
                    simulation.id = 1
                    simulation.setEnd(new DateTime())
                    return [simulation]
                default: return []
            }
        }

        treeModel.metaClass.getAllBatchRuns = {->
            return [new BatchRun(name: "test")]
        }

        treeModel.metaClass.getValue = {Parameterization p, int columnIndex ->
            return "column " + columnIndex
        }
        treeModel.buildTreeNodes()
        return treeModel
    }



    public static Parameterization createStubParameterization(int index, Status status) {
        Parameterization parameterization = new Parameterization("param" + index)
        parameterization.id = index
        Person person = new Person(username: "username" + index)
        parameterization.setCreator(person)
        parameterization.setCreationDate(new DateTime())
        Person person2 = new Person(username: "modificator" + index)
        parameterization.setLastUpdater(person2)
        parameterization.setModificationDate(new DateTime())
        parameterization.status = status
        return parameterization

    }

    public static Simulation createNewSimulation() {
        Simulation simulation = new Simulation("simulation2")
        simulation.modelClass = ApplicationModel
        simulation.parameterization = new Parameterization("param1")
        simulation.template = new ResultConfiguration("result1")
        simulation.id = 2
        simulation.setEnd(new DateTime())
        return simulation
    }
}
