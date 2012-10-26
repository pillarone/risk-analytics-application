package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.parameter.MultiDimensionalParameterValue
import org.pillarone.riskanalytics.core.parameter.Parameter
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.DynamicComposedComponentTableTreeNode
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObjectClassifier
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.MultiDimensionalParameterDimension
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel

class ParameterizationTableTreeModelTests extends GroovyTestCase {

    Parameterization parameterization
    ModelStructure structure
    Model model
    ParameterViewModel viewModel

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        parameterization.removeListener(viewModel)
        LocaleResources.clearTestMode()
    }

    void prepareCoreModel() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        model = new CoreModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('CoreStructure'))
        parameterization.load()
        structure.load()

        viewModel = new TestParameterViewModel(model, parameterization, structure)
        parameterization.addListener(viewModel)
    }

    void prepareMultiPeriodCoreModel() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreMultiPeriodParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        model = new CoreModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreMultiPeriodParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('CoreStructure'))
        parameterization.load()
        structure.load()

        viewModel = new TestParameterViewModel(model, parameterization, structure)
        parameterization.addListener(viewModel)
    }

    void prepareApplicationModel() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        model = new ApplicationModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('ApplicationStructure'))
        parameterization.load()
        structure.load()

        viewModel = new TestParameterViewModel(model, parameterization, structure)
        parameterization.addListener(viewModel)
    }

    void testSimpleSetValueAt() {

        prepareApplicationModel()

        assertEquals ExampleEnum.FIRST_VALUE, parameterization.getParameters('parameterComponent:parmEnumParameter').get(0).businessObject
        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def enumNode = tableModel.root.getChildAt(1).getChildAt(1)
        assertEquals ComponentUtils.getNormalizedName("parmEnumParameter"), enumNode.displayName
        assertEquals 'First value', tableModel.getValueAt(enumNode, 1)

        tableModel.setValueAt('SECOND_VALUE', enumNode, 1)
        assertEquals 'SECOND_VALUE', tableModel.getValueAt(enumNode, 1)
        parameterization.save()
        assertEquals ExampleEnum.SECOND_VALUE, parameterization.getParameters('parameterComponent:parmEnumParameter').get(0).businessObject

        assertEquals initialParameterCount, Parameter.count()
    }

    void testSetValueAtClassifierNode() {

        prepareMultiPeriodCoreModel()

        ParameterObjectParameterHolder parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.TYPE0, parameterObjectParameter.classifier

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def parameterObjectNode = tableModel.root.getChildAt(0).getChildAt(0)
        def oldParameter = parameterization.getParameterHolder(parameterObjectNode.parameterPath, 0)
        assertEquals 5, parameterObjectNode.childCount
        def classifierNode = parameterObjectNode.getChildAt(0)
        assertEquals 'type', classifierNode.displayName
        assertEquals 'TYPE0', tableModel.getValueAt(classifierNode, 1)
        assertEquals 'TYPE1', tableModel.getValueAt(classifierNode, 2)

        tableModel.setValueAt('TYPE1', classifierNode, 1)

        assertEquals 3, parameterObjectNode.childCount
        assertTrue parameterization.parameterHolders.contains(oldParameter)

        assertNull classifierNode.parent

        assertEquals 'TYPE1', tableModel.getValueAt(parameterObjectNode.getChildAt(0), 1)
        parameterization.save()
        parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.TYPE1, parameterObjectParameter.classifier
        assertNotNull parameterObjectParameter.classifierParameters.find {it.key == 'p1'}
        assertNotNull parameterObjectParameter.classifierParameters.find {it.key == 'p2'}

        assertEquals initialParameterCount, Parameter.count()
    }

    void testSetValueAtClassifierNodeWithStructureChanges() {

        prepareMultiPeriodCoreModel()

        ParameterObjectParameterHolder parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.TYPE0, parameterObjectParameter.classifier

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def parameterObjectNode = tableModel.root.getChildAt(0).getChildAt(0)
        def oldParameter = parameterization.getParameterHolder(parameterObjectNode.parameterPath, 0)
        assertEquals 5, parameterObjectNode.childCount
        def classifierNode = parameterObjectNode.getChildAt(0)
        assertEquals 'type', classifierNode.displayName
        assertEquals 'TYPE0', tableModel.getValueAt(classifierNode, 1)
        assertEquals 'TYPE1', tableModel.getValueAt(classifierNode, 2)

        //change the classifier multiple times must not have an influence
        tableModel.setValueAt('TYPE1', parameterObjectNode.getChildAt(0), 1)
        tableModel.setValueAt('TYPE2', parameterObjectNode.getChildAt(0), 1)

        assertEquals 4, parameterObjectNode.childCount
        assertTrue parameterization.parameterHolders.contains(oldParameter)

        assertNull classifierNode.parent

        assertEquals 'TYPE2', tableModel.getValueAt(parameterObjectNode.getChildAt(0), 1)
        parameterization.save()
        parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.TYPE2, parameterObjectParameter.classifier
        assertNotNull parameterObjectParameter.classifierParameters.find {it.key == 'p1'}
        assertNotNull parameterObjectParameter.classifierParameters.find {it.key == 'p2'}
        assertNotNull parameterObjectParameter.classifierParameters.find {it.key == 'p3'}

        assertEquals initialParameterCount + 1, Parameter.count()
    }

    void testSetValueAtMultiDimensionalNode() {

        prepareApplicationModel()

        int initialParameterCount = Parameter.count()
        int mdpValueCount = MultiDimensionalParameterValue.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull "no root", tableModel.root

        def mdpNode = tableModel.root.getChildAt(1).getChildAt(0)
        assertTrue "Wrong type:${mdpNode.class.name}", mdpNode instanceof MultiDimensionalParameterizationTableTreeNode
        def oldParameter = parameterization.getParameterHolder(mdpNode.parameterPath, 0)
        AbstractMultiDimensionalParameter parameterInstance = oldParameter.businessObject
        assertEquals 0, parameterInstance.getValueAt(0, 0)
        parameterInstance.setValueAt(10, 0, 0)

        tableModel.setValueAt(parameterInstance, mdpNode, 1)

        assertTrue "old parameter does not exist anymore", parameterization.parameterHolders.contains(oldParameter)

        parameterization.save()
        MultiDimensionalParameterHolder multiDimensionalParameter = parameterization.getParameters('parameterComponent:parmMultiDimensionalParameter').get(0)
        assertEquals 4, multiDimensionalParameter.businessObject.values.flatten().size()
        assertEquals 10, multiDimensionalParameter.businessObject.values.flatten()[0]

        assertEquals initialParameterCount, Parameter.count()
        assertEquals mdpValueCount, MultiDimensionalParameterValue.count()
    }

    void testSetValueAtMultiDimensionalNodeWithDimensionChange() {

        prepareApplicationModel()

        MultiDimensionalParameterHolder multiDimensionalParameter = parameterization.getParameters('parameterComponent:parmMultiDimensionalParameter').get(0)

        assertEquals 4, multiDimensionalParameter.businessObject.values.flatten().size()

        int initialParameterCount = Parameter.count()
        int multidimensionalValueCount = MultiDimensionalParameterValue.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def mdpNode = tableModel.root.getChildAt(1).getChildAt(0)
        assertTrue mdpNode instanceof MultiDimensionalParameterizationTableTreeNode
        def oldParameter = parameterization.getParameterHolder(mdpNode.parameterPath, 0)
        AbstractMultiDimensionalParameter parameterInstance = oldParameter.businessObject
        assertEquals 0, parameterInstance.getValueAt(0, 0)
        parameterInstance.setDimension(new MultiDimensionalParameterDimension(3, 2))
        parameterInstance.setValueAt(0, 0, 2)
        parameterInstance.setValueAt(0, 1, 2)
        //change the instance multiple times must not have an influence
        tableModel.setValueAt(parameterInstance, mdpNode, 1)
        parameterInstance.setValueAt(22, 0, 2)
        parameterInstance.setValueAt(33, 1, 2)

        tableModel.setValueAt(parameterInstance, mdpNode, 1)

        assertTrue parameterization.parameterHolders.contains(oldParameter)

        parameterization.save()
        multiDimensionalParameter = parameterization.getParameters('parameterComponent:parmMultiDimensionalParameter').get(0)
        assertEquals 6, multiDimensionalParameter.businessObject.values.flatten().size()

        assertEquals initialParameterCount, Parameter.count()
        assertEquals multidimensionalValueCount + 2, MultiDimensionalParameterValue.count()
    }

    void testSetValueAtNestedMultiDimensionalNode() {

        prepareApplicationModel()

        ParameterObjectParameterHolder parameterObjectParameter = parameterization.getParameters('parameterComponent:parmNestedMdp').get(0)
        assertEquals ExampleParameterObjectClassifier.NESTED_MDP, parameterObjectParameter.classifier

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def parameterObjectNode = tableModel.root.getChildAt(1).getChildAt(2)
        def oldParameter = parameterization.getParameterHolder(parameterObjectNode.parameterPath, 0)
        assertEquals 2, parameterObjectNode.childCount
        def mdpNode = parameterObjectNode.getChildAt(1)
        assertEquals 'mdp', mdpNode.displayName
        AbstractMultiDimensionalParameter parameterInstance = parameterization.getParameterHolder(mdpNode.parameterPath, 0).businessObject
        parameterInstance.setValueAt(10, 0, 0)

        tableModel.setValueAt(parameterInstance, mdpNode, 1)

        assertEquals 2, parameterObjectNode.childCount
        assertTrue parameterization.parameterHolders.contains(oldParameter)

        parameterization.save()
        parameterObjectParameter = parameterization.getParameters('parameterComponent:parmNestedMdp').get(0)
        MultiDimensionalParameterHolder dependencyMatrix = parameterObjectParameter.classifierParameters.get('mdp')
        assertNotNull dependencyMatrix

        assertEquals 10, dependencyMatrix.businessObject.values.flatten()[0]
        assertEquals initialParameterCount, Parameter.count()
    }

    void testSetNestedParameterObjects() {

        prepareCoreModel()

        ParameterObjectParameterHolder parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.TYPE0, parameterObjectParameter.classifier

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def parameterObjectNode = tableModel.root.getChildAt(0).getChildAt(0)
        def oldParameter = parameterization.getParameterHolder(parameterObjectNode.parameterPath, 0)
        assertEquals 3, parameterObjectNode.childCount
        def classifierNode = parameterObjectNode.getChildAt(0)
        assertEquals 'type', classifierNode.displayName
        assertEquals 'TYPE0', tableModel.getValueAt(classifierNode, 1)

        //change the classifier multiple times must not have an influence
        tableModel.setValueAt('TYPE1', parameterObjectNode.getChildAt(0), 1)
        tableModel.setValueAt('NESTED_PARAMETER_OBJECT', parameterObjectNode.getChildAt(0), 1)

        assertEquals 2, parameterObjectNode.childCount
        assertTrue parameterization.parameterHolders.contains(oldParameter)

        assertNull classifierNode.parent
        def nestedDistributionNode = parameterObjectNode.getChildAt(1)
        assertTrue nestedDistributionNode instanceof ParameterObjectParameterTableTreeNode
        assertEquals 3, nestedDistributionNode.childCount

        tableModel.setValueAt('TYPE2', nestedDistributionNode.getChildAt(0), 1)
        assertEquals 4, nestedDistributionNode.childCount

        assertEquals 'NESTED_PARAMETER_OBJECT', tableModel.getValueAt(parameterObjectNode.getChildAt(0), 1)
        parameterization.save()
        parameterObjectParameter = parameterization.getParameters('exampleInputOutputComponent:parmParameterObject').get(0)
        assertEquals ExampleParameterObjectClassifier.NESTED_PARAMETER_OBJECT, parameterObjectParameter.classifier
        assertNotNull parameterObjectParameter.classifierParameters.get('nested')

        assertEquals initialParameterCount + 3, Parameter.count()
    }

    void testAddDynamicComponent() {

        prepareCoreModel()

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def dynamicNode = tableModel.root.getChildAt(1)
        assertTrue dynamicNode instanceof DynamicComposedComponentTableTreeNode
        assertEquals 1, dynamicNode.childCount

        def newComponent = dynamicNode.component.createDefaultSubComponent()
        newComponent.name = "subComponent0"
        parameterization.addComponent("dynamicComponent:" + newComponent.name, newComponent)

        assertEquals 2, dynamicNode.childCount
        parameterization.save()

        boolean pathFound = false
        parameterization.parameters*.path.each {String path ->
            if (path.contains("subComponent0")) {
                pathFound = true
                assertTrue path.startsWith("dynamicComponent")
            }
        }
        assertTrue pathFound

        assertEquals initialParameterCount + 8, Parameter.count()
    }

    void testAddDynamicComponentMultiPeriod() {

        prepareMultiPeriodCoreModel()

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root

        def dynamicNode = tableModel.root.getChildAt(1)
        assertTrue dynamicNode instanceof DynamicComposedComponentTableTreeNode
        assertEquals 1, dynamicNode.childCount

        def newComponent = dynamicNode.component.createDefaultSubComponent()
        newComponent.name = "subTestComponent"
        parameterization.addComponent("dynamicComponent:" + newComponent.name, newComponent)

        assertEquals 2, dynamicNode.childCount
        parameterization.save()

        def parms = parameterization.parameters.findAll {it -> it.path.contains("subTestComponent:parmParameterObject")}.sort {it.periodIndex}
        assertEquals 2, parms.size()
        assertEquals 0, parms[0].periodIndex
        assertEquals 1, parms[1].periodIndex

        assertEquals initialParameterCount + 16, Parameter.count()
    }

    void testRemoveDynamicComponent() {

        prepareCoreModel()

        int initialParameterCount = Parameter.count()

        def tableModel = viewModel.getActualTableTreeModel()

        assertNotNull tableModel.root
        def dynamicNode = tableModel.root.getChildAt(1)

        def newComponent = dynamicNode.component.createDefaultSubComponent()
        newComponent.name = "subComponent0"
        parameterization.addComponent("dynamicComponent:" + newComponent.name, newComponent)

        def contractNode = dynamicNode.getChildAt(0)
        assertTrue contractNode instanceof ComponentTableTreeNode
        assertEquals 2, dynamicNode.childCount

        parameterization.removeComponent("dynamicComponent:" + newComponent.name)
        assertEquals 1, dynamicNode.childCount
        parameterization.save()

        assertEquals initialParameterCount, Parameter.count()
    }

    void testKeepOldParameterValues() {
        prepareCoreModel()

        def tableModel = viewModel.getActualTableTreeModel()

        def parameterObjectNode = tableModel.root.getChildAt(0).getChildAt(0)
        tableModel.setValueAt("TYPE1", parameterObjectNode.getChildAt(0), 1)
        assertEquals 3, parameterObjectNode.childCount

        tableModel.setValueAt("TYPE2", parameterObjectNode.getChildAt(0), 1)
        assertEquals 4, parameterObjectNode.childCount
        ITableTreeNode p1 = parameterObjectNode.getChildByName("p1")
        tableModel.setValueAt(10d, p1, 1)

        tableModel.setValueAt("TYPE1", parameterObjectNode.getChildAt(0), 1)
        p1 = parameterObjectNode.getChildByName("p1")
        assertEquals "value not preserved", 10d, tableModel.getValueAt(p1, 1)

    }
}
