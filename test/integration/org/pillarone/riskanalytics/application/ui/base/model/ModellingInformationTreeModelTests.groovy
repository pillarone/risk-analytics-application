package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.event.ITreeModelListener
import com.ulcjava.base.application.event.TreeModelEvent
import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import com.ulcjava.base.application.tree.ITreeNode
import groovy.mock.interceptor.MockFor
import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ModellingInformationTreeModelTests extends GroovyTestCase {

    MockFor modelStructureMock
    MockFor factoryMock

    void setUp() {
        LocaleResources.setTestMode()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testSingleParam() {
        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'))], [])
        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '1', node.item.versionNumber.toString()
            }
        }
    }

    void testSimpleParamStructure() {
        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                assertEquals '2', node.getChildAt(0).item.versionNumber.toString()
                assertEquals '1', node.getChildAt(1).item.versionNumber.toString()
            }
        }
    }

    void testSimpleParamStructureWithTenNodes() {
        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('3')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('4')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('5')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('6')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('7')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('8')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('9')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('10')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('11'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '11', node.item.versionNumber.toString()

                assertEquals 10, node.childCount
            }
        }
    }

    void testParamsWithSubversionsStructure() {
        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('1.1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()

                assertEquals 1, subNode2.childCount
                assertEquals '1.1', subNode2.getChildAt(0).item.versionNumber.toString()
            }
        }
    }

    void testTopLevelSubversionParam() {
        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1')),
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '2.1', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()

            }
        }
    }

    void testAddNodeSimple() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'))], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '2', node.item.versionNumber.toString()

                assertEquals 1, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '1', subNode1.item.versionNumber.toString()

            }
        }

    }

    void testAddNode() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()
            }
        }
    }

    void testAddNodeWithDifferentName() {

        prepareMocks([new Parameterization(name: 'foo', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'bar', versionNumber: new VersionNumber('1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'foo', versionNumber: new VersionNumber('2'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 2, paramsNode.childCount
            }
        }

    }

    void testAddNodeWithNewName() {

        prepareMocks([new Parameterization(name: 'foo', versionNumber: new VersionNumber('1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'bar', versionNumber: new VersionNumber('1'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 2, paramsNode.childCount
            }
        }

    }

    void testAddSubversionNode() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()

            }
        }

    }

    void testAddSubversionNode2() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1.1'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).item.versionNumber.toString()

                ITreeNode subSubNode = subNode1.getChildAt(0)
                assertEquals 1, subSubNode.childCount
                assertEquals '2.1.1', subSubNode.getChildAt(0).item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()

            }
        }
    }

    void testAddNodeWithTopLevelSubversionNode() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel))

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '3', node.item.versionNumber.toString()

                assertEquals 2, node.childCount
                ITreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).item.versionNumber.toString()

                ITreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.item.versionNumber.toString()

            }
        }
    }

    void testRemoveSingleNode() {

        Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        prepareMocks([parameterization], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.removeNodeForItem(parameterization)

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 0, paramsNode.childCount
            }
        }
    }

    void testRemoveNode() {

        Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization2 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'), modelClass: CoreModel)
        Parameterization parameterization3 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel)
        prepareMocks([parameterization, parameterization2, parameterization3], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.removeNodeForItem(parameterization3)

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '2', node.item.versionNumber.toString()

                assertEquals 1, node.childCount
                def child = node.getChildAt(0)
                assertEquals 'Name', child.item.name
                assertEquals '1', child.item.versionNumber.toString()
            }
        }
    }

    void testRemoveSubNode() {

        Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization2 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'), modelClass: CoreModel)
        Parameterization parameterization3 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1.1'), modelClass: CoreModel)
        prepareMocks([parameterization, parameterization2, parameterization3], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTreeModel()
                model.removeNodeForItem(parameterization3)

                DefaultMutableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.item.name
                assertEquals '2', node.item.versionNumber.toString()

                assertEquals 1, node.childCount
                assertEquals 'Name', node.getChildAt(0).item.name
                assertEquals '1', node.getChildAt(0).item.versionNumber.toString()

                assertEquals 0, node.getChildAt(0).childCount
            }
        }
    }


    void testRefresh_AddingItem() {
        Parameterization parameterization1 = new Parameterization(name: 'Name1', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization2 = new Parameterization(name: 'Name2', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization3 = new Parameterization(name: 'Name3', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization4 = new Parameterization(name: 'Name4', versionNumber: new VersionNumber('1'), modelClass: CoreModel)

        ModellingInformationTreeModel model = null
        prepareMocks([parameterization1, parameterization2, parameterization3], [])
        modelStructureMock.use {
            factoryMock.use {
                model = new ModellingInformationTreeModel()
            }
        }




        TreeModelEvent treeModelEvent = null

        model.addTreeModelListener([
                treeStructureChanged: {TreeModelEvent event -> treeModelEvent = event}
        ] as ITreeModelListener)

        assertEquals "wrong number of parameter childs", 3, model.root.getChildAt(0).getChildAt(0).childCount
        prepareMocks([parameterization1, parameterization2, parameterization3, parameterization4], [])
        modelStructureMock.use {
            factoryMock.use {
                model.refresh()
            }
        }
        assertEquals "wrong number of parameter childs", 4, model.root.getChildAt(0).getChildAt(0).childCount
        assertNotNull "No event fired", treeModelEvent
    }

    void testRefresh_RemovingItem() {
        Parameterization parameterization1 = new Parameterization(name: 'Name1', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization2 = new Parameterization(name: 'Name2', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization3 = new Parameterization(name: 'Name3', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization4 = new Parameterization(name: 'Name4', versionNumber: new VersionNumber('1'), modelClass: CoreModel)

        ModellingInformationTreeModel model = null
        prepareMocks([parameterization1, parameterization2, parameterization3, parameterization4], [])
        modelStructureMock.use {
            factoryMock.use {
                model = new ModellingInformationTreeModel()
                assertEquals "wrong number of parameter childs", 4, model.root.getChildAt(0).getChildAt(0).childCount
            }
        }

        TreeModelEvent treeModelEvent = null

        model.addTreeModelListener([
                treeNodesChanged: {TreeModelEvent event -> treeModelEvent = event},
                treeStructureChanged: {TreeModelEvent event -> treeModelEvent = event},
                treeNodesRemoved: {TreeModelEvent event -> treeModelEvent = event}
        ] as ITreeModelListener)

        prepareMocks([parameterization1, parameterization2, parameterization3], [])
        modelStructureMock.use {
            factoryMock.use {
                model.refresh()
                assertEquals "wrong number of parameter childs", 3, model.root.getChildAt(0).getChildAt(0).childCount
                assertNotNull "No event fired", treeModelEvent
            }
        }
    }

    private void prepareMocks(params, resultConfigurations) {
        modelStructureMock = new MockFor(ModelStructure)
        modelStructureMock.demand.findAllModelClasses { CoreModel }

        factoryMock = new MockFor(ModellingItemFactory)
        factoryMock.demand.getParameterizationsForModel {modelClass ->
            params
        }
        factoryMock.demand.getResultConfigurationsForModel {modelClass ->
            resultConfigurations
        }
        factoryMock.demand.getActiveSimulationsForModel {modelClass ->
            []
        }
    }

}