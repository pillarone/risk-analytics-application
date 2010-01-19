package org.pillarone.riskanalytics.application.dataaccess.item

import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ModelItem
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.ItemComparator
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.ConfigObjectBasedModellingItem

class ModellingItemFactory {

    static Map getSimulationInstances() {
        def map = UserContext.getAttribute("simulationInstances")
        if (map == null) {
            map = [:]
            UserContext.setAttribute("simulationInstances", map)
        }
        map
    }

    static Map getItemInstances() {
        def map = UserContext.getAttribute("itemInstances")
        if (map == null) {
            map = [:]
            UserContext.setAttribute("itemInstances", map)
        }
        map
    }

    static Parameterization getParameterization(ParameterizationDAO dao) {
        return getItem(dao)
    }

    static ModelItem getModelItem(ModelDAO dao) {
        return getItem(dao)
    }

    static List getParameterizationsForModel(Class modelClass) {
        ParameterizationDAO.findAllByModelClassName(modelClass.name).collect {
            getItem(it)
        }
    }

    static List getNewestParameterizationsForModel(Class modelClass) {
        def result = []
        def criteria = ParameterizationDAO.createCriteria()
        def parameterizationNames = criteria.list {
            eq('modelClassName', modelClass.name)
            projections {
                distinct('name')
            }
        }

        parameterizationNames.each {
            String name = it
            criteria = ParameterizationDAO.createCriteria()
            def highestVesion = criteria.list {
                eq('modelClassName', modelClass.name)
                eq('name', name)
                projections {
                    max('itemVersion')
                }
            }
            result << ParameterizationDAO.findByNameAndItemVersion(name, highestVesion[0])
        }
        result.collect {getItem(it)}
    }

    static ModellingItem getNewestModelItem(String modelName) {
        def result = []
        def criteria = ModelDAO.createCriteria()
        def highestVersion = criteria.list {
            eq('name', modelName)
            projections {
                max('itemVersion')
            }
        }
        result << ModelDAO.findByNameAndItemVersion(modelName, highestVersion[0])
        if (result[0]) {
            return getItem(result[0])
        }
        return null
    }

    static List getNewestResultConfigurationsForModel(Class modelClass) {
        def result = []
        def criteria = ResultConfigurationDAO.createCriteria()
        def templateNames = criteria.list {
            eq('modelClassName', modelClass.name)
            projections {
                distinct('name')
            }
        }

        templateNames.each {
            String name = it
            criteria = ResultConfigurationDAO.createCriteria()
            def highestVesion = criteria.list {
                eq('modelClassName', modelClass.name)
                eq('name', name)
                projections {
                    max('itemVersion')
                }
            }
            result << ResultConfigurationDAO.findByNameAndItemVersion(name, highestVesion[0])
        }
        result.collect {getItem(it)}
    }

    static List getNewestModulStructureForModel(Class modelClass) {
        def result = []
        def criteria = ModelStructureDAO.createCriteria()
        def modelStructureNames = criteria.list {
            eq('modelClassName', modelClass.name)
            projections {
                distinct('name')
            }
        }

        modelStructureNames.each {
            String name = it
            criteria = ModelStructureDAO.createCriteria()
            def highestVesion = criteria.list {
                eq('modelClassName', modelClass.name)
                eq('name', name)
                projections {
                    max('itemVersion')
                }
            }
            result << ModelStructureDAO.findByNameAndItemVersion(name, highestVesion[0])
        }
        result.collect {getItem(it)}
    }

    static ModellingItem createItem(String name, ConfigObject data, Class itemClass) {

        def item

        if (itemClass == Parameterization) {
            item = ParameterizationHelper.createParameterizationFromConfigObject(data, name)
            name = item.name
        } else if (itemClass == ResultConfiguration) {
            item = new ResultConfiguration(data, name)
            name = item.name
        } else {
            if (data.containsKey('displayName')) {
                name = data.displayName
            }
            item = itemClass.newInstance([name] as Object[])
            item.data = data
            item.modelClass = data.model
        }
        if (item instanceof Parameterization || item instanceof ResultConfiguration) {
            if (item.creationDate == null) {
                item.creationDate = new Date()
            }
        }

        def criteria = item.daoClass.createCriteria()
        def highestVesion = criteria.get {
            eq('modelClassName', data.model.name)
            eq('name', name)
            projections {
                max('itemVersion')
            }
        }
        if (highestVesion != null) {
            boolean equals = false
            item.daoClass.withTransaction {status ->
                def existingDao = item.daoClass.findByNameAndItemVersion(name, highestVesion)
                ModellingItem existingItem = getItem(existingDao)
                if (!existingItem.isLoaded()) {
                    existingItem.load()
                }
                equals = ItemComparator.contentEquals(item, existingItem)
                item.versionNumber = VersionNumber.incrementVersion(existingItem)
            }
            if (equals) {
                return null
            }
        }
        def id = item.save()
        getItemInstances()[key(itemClass, id)] = item

        item
    }

    static ModellingItem createParameterization(String name, ConfigObject data, Class itemClass, VersionNumber versionNumber) {
        def item = ParameterizationHelper.createParameterizationFromConfigObject(data, name)
        item.versionNumber = versionNumber
        def id = item.save()
        getItemInstances()[key(itemClass, id)] = item
        item
    }

    static ResultConfiguration getResultConfiguration(ResultConfigurationDAO dao) {
        return getItem(dao)
    }


    static List getResultConfigurationsForModel(Class modelClass) {
        ResultConfigurationDAO.findAllByModelClassName(modelClass.name).collect {
            getItem(it)
        }
    }

    static ModelStructure getModelStructure(ModelStructureDAO dao) {
        return getItem(dao)
    }

    static Simulation getSimulation(String name, Class modelClass) {
        return getItem(Simulation, modelClass, name)
    }

    /**
     * @return all SimulationRuns for this model with toBeDeleted==false
     */
    static List getActiveSimulationsForModel(Class modelClass) {
        SimulationRun.findAllByModel(modelClass.name).
                findAll {SimulationRun run ->
            !run.toBeDeleted && run.endTime != null
        }.
                collect {SimulationRun run ->
            getItem(Simulation, modelClass, run.name)
        }
    }

    static ModellingItem copyItem(ModellingItem oldItem, String newName) {
        ModellingItem newItem = oldItem.class.newInstance([newName] as Object[])

        oldItem.load()
        if (oldItem.data != null) {
            newItem.data = oldItem.data.merge(new ConfigObject())
        }
        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem copyItem(Parameterization oldItem, String newName) {
        Parameterization newItem = new Parameterization(newName)

        List newParameters = ParameterizationHelper.copyParameters(oldItem.parameters)
        newParameters.each {
            newItem.addParameter(it)
        }
        newItem.periodCount = oldItem.periodCount
        newItem.periodLabels = oldItem.periodLabels
        newItem.modelClass = oldItem.modelClass
        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem copyItem(ResultConfiguration oldItem, String newName) {
        ResultConfiguration newItem = copy(oldItem, newName)

        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    private static ResultConfiguration copy(ResultConfiguration oldItem, String newName) {
        ResultConfiguration newItem = new ResultConfiguration(newName)

        for (PacketCollector collector in oldItem.collectors) {
            newItem.collectors << new PacketCollector(path: collector.path, mode: collector.mode)
        }
        newItem.comment = oldItem.comment
        newItem.modelClass = oldItem.modelClass
        return newItem
    }

    static ModellingItem incrementVersion(ModellingItem item) {
        ConfigObjectBasedModellingItem newItem = item.class.newInstance([item.name] as Object[])

        item.load()
        if (item.data != null) {
            newItem.data = item.data.merge(new ConfigObject())
            newItem.versionNumber = VersionNumber.incrementVersion(item)
        }
        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(Parameterization item) {
        Parameterization newItem = new Parameterization(item.name)

        List newParameters = ParameterizationHelper.copyParameters(item.parameters)
        newParameters.each {
            newItem.addParameter(it)
        }
        newItem.periodCount = item.periodCount
        newItem.periodLabels = item.periodLabels
        newItem.modelClass = item.modelClass
        newItem.versionNumber = VersionNumber.incrementVersion(item)

        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(ResultConfiguration oldItem) {
        ResultConfiguration newItem = copy(oldItem, oldItem.name)
        newItem.versionNumber = VersionNumber.incrementVersion(oldItem)

        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(ModelItem item) {
        ModelItem newItem = new ModelItem(item.name)
        item.load()
        if (item.srcCode != null) {
            newItem.srcCode = item.srcCode
            newItem.versionNumber = VersionNumber.incrementVersion(item)
        }
        def newID = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newID)] = newItem
        return newItem
    }

    static ModellingItem setItemVersion(Parameterization item, VersionNumber newVersion) {
        Parameterization newItem = new Parameterization(item.name)

        List newParameters = ParameterizationHelper.copyParameters(item.parameters)
        newParameters.each {
            newItem.addParameter(it)
        }
        newItem.periodCount = item.periodCount
        newItem.periodLabels = item.periodLabels
        newItem.modelClass = item.modelClass
        newItem.versionNumber = VersionNumber.incrementVersion(item)

        def newId = newItem.save()
        newItem.load()
        getItemInstances()[key(newItem.class, newId)] = newItem
        return newItem
    }



    private static ModellingItem getItem(Class itemClass, Class modelClass, String itemName) {
        String key = key(itemClass, modelClass, itemName)
        ModellingItem item = getSimulationInstances()[key]
        if (!item) {
            item = itemClass.newInstance([itemName] as Object[])
            getSimulationInstances()[key] = item
        }
        return item
    }

    private static ModellingItem getItem(ParameterizationDAO dao) {
        Parameterization item = getItemInstances()[key(Parameterization, dao.id)]
        if (!item) {
            item = new Parameterization(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            // todo fja load a parameters as lazy
            // PMO-645 set valid  for parameterization check
            item.valid = dao.valid
            getItemInstances()[key(Parameterization, dao.id)] = item
        }
        item
    }

    private static ModellingItem getItem(ModelDAO dao) {
        ModelItem item = getItemInstances()[key(ModelItem, dao.id)]
        if (!item) {
            item = new ModelItem(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            getItemInstances()[key(ModelItem, dao.id)] = item
        }
        item
    }

    private static ModellingItem getItem(ResultConfigurationDAO dao) {
        ResultConfiguration item = getItemInstances()[key(ResultConfiguration, dao.id)]
        if (!item) {
            item = new ResultConfiguration(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            getItemInstances()[key(ResultConfiguration, dao.id)] = item
        }
        item
    }

    private static ModellingItem getItem(ModelStructureDAO dao) {
        ModelStructure item = getItemInstances()[key(ModelStructure, dao.id)]
        if (!item) {
            item = new ModelStructure(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            getItemInstances()[key(ModelStructure, dao.id)] = item
        }
        item
    }

    private static def key(Class itemClass, Class modelClass, String itemName) {
        return "${itemClass?.simpleName}_${modelClass?.simpleName}_$itemName".toString()
    }

    private static def key(Class itemClass, Long daoId) {
        return "${itemClass?.simpleName}_$daoId".toString()
    }

    static void remove(ModellingItem item) {
        getSimulationInstances().remove(key(item.class, item.modelClass, item.name))
    }

    static void clear() {
        getSimulationInstances().clear()
        getItemInstances().clear()
    }

}
