package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import groovy.mock.interceptor.MockFor
import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import models.application.ApplicationModel
import models.migratableCore.MigratableCoreModel
import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import groovy.mock.interceptor.StubFor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModelTests extends GroovyTestCase {

    StubFor modelStructureMock
    StubFor factoryMock

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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '1', node.abstractUIItem.item.versionNumber.toString()
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                assertEquals '2', node.getChildAt(0).abstractUIItem.item.versionNumber.toString()
                assertEquals '1', node.getChildAt(1).abstractUIItem.item.versionNumber.toString()
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '11', node.abstractUIItem.item.versionNumber.toString()

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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, subNode2.childCount
                assertEquals '1.1', subNode2.getChildAt(0).abstractUIItem.item.versionNumber.toString()
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '2.1', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()

            }
        }
    }

    void testAddNodeSimple() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'))], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel(null)
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new ApplicationModel(), parameterization))

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '2', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '1', subNode1.abstractUIItem.item.versionNumber.toString()

            }
        }

    }

    void testAddNode() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))

//                model.addNodeForItem(new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel))

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()
            }
        }
    }

    void testAddModelNode() {

        prepareMocks([new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()

                assertEquals 3, model.root.childCount

                model.addNodeForItem(new MigratableCoreModel())

                assertEquals 4, model.root.childCount

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(1)
                assertEquals "MigratableCoreModel", modelNode.getItemClass().simpleName
            }
        }
    }

    void testAddNodeWithDifferentName() {

        prepareMocks([new Parameterization(name: 'foo', versionNumber: new VersionNumber('1')),
                new Parameterization(name: 'bar', versionNumber: new VersionNumber('1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'foo', versionNumber: new VersionNumber('2'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))

//                model.addNodeForItem(new Parameterization(name: 'foo', versionNumber: new VersionNumber('2'), modelClass: CoreModel))

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 2, paramsNode.childCount
            }
        }

    }

    void testAddNodeWithNewName() {

        prepareMocks([new Parameterization(name: 'foo', versionNumber: new VersionNumber('1'))
        ], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
//                model.addNodeForItem(new Parameterization(name: 'bar', versionNumber: new VersionNumber('1'), modelClass: CoreModel))
                Parameterization parameterization = new Parameterization(name: 'bar', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))


                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))


                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()

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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2.1.1'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))


                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount
                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subSubNode = subNode1.getChildAt(0)
                assertEquals 1, subSubNode.childCount
                assertEquals '2.1.1', subSubNode.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()

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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel)
                model.addNodeForItem(new ParameterizationUIItem(null, new CoreModel(), parameterization))


                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.abstractUIItem.item.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 2, node.childCount
                DefaultMutableTableTreeNode subNode1 = node.getChildAt(0)
                assertEquals '2', subNode1.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, subNode1.childCount
                assertEquals '2.1', subNode1.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                DefaultMutableTableTreeNode subNode2 = node.getChildAt(1)
                assertEquals '1', subNode2.abstractUIItem.item.versionNumber.toString()

            }
        }
    }

    void testRemoveSingleNode() {

        Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        prepareMocks([parameterization], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel(null)
                model.buildTreeNodes()
                ParameterizationUIItem parameterizationUIItem = new ParameterizationUIItem(null, new CoreModel(), parameterization)
                model.removeNodeForItem(parameterizationUIItem)

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                ParameterizationUIItem parameterizationUIItem3 = new ParameterizationUIItem(null, new CoreModel(), parameterization3)
                model.removeNodeForItem(parameterizationUIItem3)

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '2', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, node.childCount
                def child = node.getChildAt(0)
                assertEquals 'Name', child.name
                assertEquals '1', child.abstractUIItem.item.versionNumber.toString()
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
                def model = new ModellingInformationTableTreeModel()
                model.buildTreeNodes()
                ParameterizationUIItem parameterizationUIItem = new ParameterizationUIItem(null, new CoreModel(), parameterization3)
                model.removeNodeForItem(parameterizationUIItem)

                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 1, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '2', node.abstractUIItem.item.versionNumber.toString()

                assertEquals 1, node.childCount
                assertEquals 'Name', node.getChildAt(0).name
                assertEquals '1', node.getChildAt(0).abstractUIItem.item.versionNumber.toString()

                assertEquals 0, node.getChildAt(0).childCount
            }
        }
    }

    void testWorkflowNodeWithExistingName() {
        Parameterization parameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('1'), modelClass: CoreModel)
        Parameterization parameterization2 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('2'), modelClass: CoreModel)
        Parameterization parameterization3 = new Parameterization(name: 'Name', versionNumber: new VersionNumber('3'), modelClass: CoreModel)
        prepareMocks([parameterization, parameterization2, parameterization3], [])

        modelStructureMock.use {
            factoryMock.use {
                def model = new ModellingInformationTableTreeModel(new RiskAnalyticsMainModel())
                model.buildTreeNodes()
                final Parameterization workflowParameterization = new Parameterization(name: 'Name', versionNumber: new VersionNumber('R1'), modelClass: CoreModel)
                model.addNodeForItem(workflowParameterization)


                DefaultMutableTableTreeNode modelNode = model.root.getChildAt(0)
                DefaultMutableTableTreeNode paramsNode = modelNode.getChildAt(0)
                assertEquals 2, paramsNode.childCount

                def node = paramsNode.getChildAt(0)
                assertEquals 'Name', node.name
                assertEquals '3', node.abstractUIItem.item.versionNumber.toString()
                assertEquals 2, node.childCount

                node = paramsNode.getChildAt(1)
                assertEquals 'Name', node.name
                assertEquals 'R1', node.abstractUIItem.item.versionNumber.toString()
                assertEquals 0, node.childCount
            }
        }
    }


    private void prepareMocks(params, resultConfigurations) {
        modelStructureMock = new StubFor(ModelRegistry)
        modelStructureMock.demand.getAllModelClasses(0..100) { return new HashSet([CoreModel]) }
        modelStructureMock.demand.getInstance(0..100) { return new ModelRegistry() }

        factoryMock = new StubFor(ModellingItemFactory)
        factoryMock.demand.getResources(0..100) {modelClass ->
            []
        }
        factoryMock.demand.getParameterizationsForModel(0..100) {modelClass ->
            params
        }
        factoryMock.demand.getResultConfigurationsForModel(0..100) {modelClass ->
            resultConfigurations
        }
        factoryMock.demand.getActiveSimulationsForModel(0..100) {modelClass ->
            []
        }
    }

    private ItemGroupNode getNormalNode(DefaultMutableTreeNode root) {
        def modelNode = root.getChildAt(0)
        assertEquals 3, modelNode.childCount
        def paramNode = modelNode.getChildAt(0)
        assertEquals 2, paramNode.childCount
        return paramNode.getChildAt(0)
    }
}
