package org.pillarone.riskanalytics.application.dataaccess.item

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.output.CustomTableDAO
import org.pillarone.riskanalytics.application.output.result.item.CustomTable
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.modellingitem.ModellingItemUpdater
import org.pillarone.riskanalytics.core.modellingitem.SimulationCacheItem
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.core.user.UserManagement
import org.springframework.transaction.TransactionStatus

class ModellingItemFactory {
    private static Log LOG = LogFactory.getLog(ModellingItemFactory)

    protected static Map getItemInstances() {
        Map map = UserContext.getAttribute("itemInstances") as Map
        if (map == null) {
            map = [:]
            UserContext.setAttribute("itemInstances", map)
        }
        map
    }

    protected static <T> T getItemInstance(String key) {
        itemInstances[key] as T
    }

    static Parameterization getParameterization(ParameterizationDAO dao) {
        ParameterizationDAO.withTransaction {
            dao.attach()
            getItem(dao, Thread.currentThread().contextClassLoader.loadClass(dao.modelClassName))
        }
    }

    static Resource getResourceFromDAO(ResourceDAO resourceDAO) {
        getItem(resourceDAO)
    }

    static ModelItem getModelItem(ModelDAO dao) {
        getItem(dao)
    }

    static List<Parameterization> getParameterizationsForModel(Class modelClass) {
        ParameterizationDAO.withTransaction { status ->
            ParameterizationDAO.findAllByModelClassName(modelClass.name, [sort: "name"]).collect {
                getItem(it, modelClass)
            }
        }
    }


    static List<Resource> getResources(Class resourceClass) {
        ResourceDAO.withTransaction { status ->
            ResourceDAO.findAllByResourceClassName(resourceClass.name, [sort: "name"]).collect {
                getItem(it)
            }
        }
    }

    static List<Parameterization> getNewestParameterizationsForModel(Class modelClass) {
        ParameterizationDAO.withTransaction {
            List<ParameterizationDAO> result = []
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
                List<ParameterizationDAO> parameterizations = criteria.list {
                    eq('modelClassName', modelClass.name)
                    eq('name', name)
                    order("itemVersion", "desc")
                }
                if (parameterizations.size() > 0) {
                    result << parameterizations[0]
                }
            }
            result.collect { getItem(it, modelClass) }
        }
    }

    static List<ResultConfiguration> getNewestResultConfigurationsForModel(Class modelClass) {
        ResultConfigurationDAO.withTransaction {
            List<ResultConfigurationDAO> result = []
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
                def highestVersion = criteria.list {
                    eq('modelClassName', modelClass.name)
                    eq('name', name)
                    projections {
                        max('itemVersion')
                    }
                }
                result << ResultConfigurationDAO.findByNameAndItemVersion(name, highestVersion[0])
            }
            result.collect { getItem(it) }
        }

    }

    static ModellingItem createItem(String name, ConfigObject data, Class itemClass, boolean forceImport) {
        ModellingItem item

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
                item.creationDate = new DateTime()
            }
        }

        String highestVersion = VersionNumber.getHighestNonWorkflowVersion(item)?.toString()

        if (highestVersion != null) {
            boolean equals = false
            item.daoClass.withTransaction { status ->
                def existingDao = item.daoClass.findByNameAndItemVersion(name, highestVersion)
                ModellingItem existingItem = getItem(existingDao)
                if (!existingItem.isLoaded()) {
                    existingItem.load()
                }
                equals = ItemComparator.contentEquals(item, existingItem)
                item.versionNumber = VersionNumber.incrementVersion(existingItem)
            }
            if (equals && !forceImport) {
                return null
            }
        }
        item.creator = UserManagement.currentUser
        def id = item.save()
        itemInstances[key(itemClass, id as Long)] = item
        item
    }

    static Parameterization createParameterization(String name, ConfigObject data, Class itemClass, VersionNumber versionNumber) {
        ParameterizationDAO.withTransaction { TransactionStatus status ->
            Parameterization item = ParameterizationHelper.createParameterizationFromConfigObject(data, name)
            item.versionNumber = versionNumber
            item.creator = UserManagement.currentUser
            def id = item.save()
            itemInstances[key(itemClass, id as Long)] = item
            item
        }
    }

    static ResultConfiguration getResultConfiguration(ResultConfigurationDAO dao) {
        ResultConfigurationDAO.withTransaction {
            dao.attach()
            getItem(dao)
        }
    }

    static List getResultConfigurationsForModel(Class modelClass) {
        ResultConfigurationDAO.withTransaction { status ->
            ResultConfigurationDAO.findAllByModelClassName(modelClass.name, [sort: "name"]).collect {
                getItem(it)
            }
        }
    }

    static List getResultStructuresForModel(Class modelClass) {
        ResultStructureDAO.withTransaction {
            ResultStructureDAO.findAllByModelClassName(modelClass.name).collect {
                getItem(it)
            }
        }
    }

    static List<CustomTable> getCustomTablesForModel(Class modelClass) {
        CustomTableDAO.withTransaction {
            CustomTableDAO.findAllByModelClassName(modelClass.name).collect {
                getItem(it)
            }
        }
    }

    static ModelStructure getModelStructure(ModelStructureDAO dao) {
        ModelStructureDAO.withTransaction {
            dao.attach()
            getItem(dao)
        }
    }

    static Simulation getSimulation(SimulationRun run) {
        SimulationRun.withTransaction {
            run.attach()
            if (run.parameterization) {
                run.parameterization.attach()  //PMO-2813 - attach also the sim's p14n so _its_ tags are also accessible
            }
            getItem(run)
        }
    }

    /**
     * @return all SimulationRuns for this model with toBeDeleted==false
     */
    static List<Simulation> getActiveSimulationsForModel(Class modelClass) {
        SimulationRun.withTransaction {
            SimulationRun.findAllByModel(modelClass.name).
                    findAll { SimulationRun run ->
                        !run.toBeDeleted && run.endTime != null
                    }.
                    collect { SimulationRun run ->
                        getItem(run)
                    }
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
        itemInstances[key(newItem.class, newId as Long)] = newItem
        return newItem
    }

    static ModellingItem copyItem(Parameterization oldItem, String newName) {
        Parameterization newItem = new Parameterization(newName)
        List newParameters = ParameterizationHelper.copyParameters(oldItem.parameters)
        newParameters.each {
            newItem.addParameter(it)
        }
        List comments = oldItem?.comments?.collect { it.clone() }
        comments?.each { newItem.addComment(it) }
        newItem.periodCount = oldItem.periodCount
        newItem.periodLabels = oldItem.periodLabels
        newItem.modelClass = oldItem.modelClass
        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem copyItem(Resource oldItem, String newName) {
        Resource newItem = new Resource(newName, oldItem.modelClass)
        List newParameters = ParameterizationHelper.copyParameters(oldItem.parameterHolders)
        newParameters.each {
            newItem.addParameter(it)
        }
        List comments = oldItem?.comments?.collect { it.clone() }
        comments?.each { newItem.addComment(it) }

        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem copyItem(ResultConfiguration oldItem, String newName) {
        ResultConfiguration newItem = copy(oldItem, newName)
        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    private static ResultConfiguration copy(ResultConfiguration oldItem, String newName) {
        ResultConfiguration newItem = new ResultConfiguration(newName, oldItem.modelClass)

        for (PacketCollector collector in oldItem.collectors) {
            newItem.collectors << new PacketCollector(path: collector.path, mode: collector.mode)
        }
        newItem.comment = oldItem.comment
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
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(Parameterization item) {
        Parameterization newItem = new Parameterization(item.name)
        List newParameters = ParameterizationHelper.copyParameters(item.parameters)
        newParameters.each {
            newItem.addParameter(it)
        }
        List comments = item?.comments?.collect { it.clone() }
        comments?.each { newItem.addComment(it) }

        newItem.periodCount = item.periodCount
        newItem.periodLabels = item.periodLabels
        newItem.modelClass = item.modelClass
        newItem.dealId = item.dealId
        newItem.versionNumber = VersionNumber.incrementVersion(item)

        if (item.changed) { //drop unsaved changed PMO-1985
            item.unload()
            item.load()
        }

        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(Resource item) {
        Resource newItem = new Resource(item.name, item.modelClass)
        List newParameters = ParameterizationHelper.copyParameters(item.parameterHolders)
        newParameters.each {
            newItem.addParameter(it)
        }
        List comments = item?.comments?.collect { it.clone() }
        comments?.each { newItem.addComment(it) }

        newItem.versionNumber = VersionNumber.incrementVersion(item)

        if (item.changed) { //drop unsaved changed PMO-1985
            item.unload()
            item.load()
        }

        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(ResultConfiguration oldItem) {
        ResultConfiguration newItem = copy(oldItem, oldItem.name)
        newItem.versionNumber = VersionNumber.incrementVersion(oldItem)

        def newId = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    static ModellingItem incrementVersion(ModelItem item) {
        ModelItem newItem = new ModelItem(item.name)
        item.load()
        if (item.srcCode != null) {
            newItem.srcCode = item.srcCode
            newItem.versionNumber = VersionNumber.incrementVersion(item)
            newItem.modelClass = item.modelClass
        }
        def newID = newItem.save()
        newItem.load()
        itemInstances[key(newItem.class, newID)] = newItem
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
        itemInstances[key(newItem.class, newId)] = newItem
        return newItem
    }

    private static ModellingItem getItem(ResultStructureDAO dao) {
        ResultStructure item = itemInstances[key(ResultStructure, dao.id)]
        if (!item) {
            item = new ResultStructure(dao.name, ModellingItemFactory.getClassLoader().loadClass(dao.modelClassName))
            item.versionNumber = new VersionNumber(dao.itemVersion)

            itemInstances[key(ResultStructure, dao.id)] = item
        }
        item
    }

    private static Resource getItem(ResourceDAO dao) {
        Resource item = itemInstances[key(Resource, dao.id)] as Resource
        if (!item) {
            item = new Resource(dao.name, ModellingItemFactory.classLoader.loadClass(dao.resourceClassName))
            item.versionNumber = new VersionNumber(dao.itemVersion)
            item.valid = dao.valid
            item.status = dao.status
            item.creator = dao.creator
            if (item.creator)
                item.creator.username = dao.creator.username
            item.lastUpdater = dao.lastUpdater
            if (item.lastUpdater)
                item.lastUpdater.username = dao.lastUpdater.username
            item.creationDate = dao.creationDate
            item.modificationDate = dao.modificationDate
            item.tags = dao.tags*.tag
            itemInstances[key(Resource, dao.id)] = item
        }
        item
    }

    private static CustomTable getItem(CustomTableDAO dao) {
        CustomTable item = itemInstances[key(CustomTable, dao.id)] as CustomTable
        if (!item) {
            item = new CustomTable(dao.name, ModellingItemFactory.classLoader.loadClass(dao.modelClassName))
            itemInstances[key(CustomTable, dao.id)] = item
        }
        item
    }

    private static Parameterization getItem(ParameterizationDAO dao, Class modelClass = null) {
        Parameterization item = itemInstances[key(Parameterization, dao.id)] as Parameterization
        if (!item) {
            item = new Parameterization(dao.name)
            itemInstances[key(Parameterization, dao.id)] = item
        }
        item.name = dao.name
        item.versionNumber = new VersionNumber(dao.itemVersion)
        // PMO-645 set valid  for parameterization check
        item.valid = dao.valid
        item.status = dao.status
        item.dealId = dao.dealId
        if (modelClass != null) {
            item.modelClass = modelClass
            item.creator = dao.creator
            if (item.creator)
                item.creator.username = dao.creator.username
            item.lastUpdater = dao.lastUpdater
            if (item.lastUpdater)
                item.lastUpdater.username = dao.lastUpdater.username
            item.creationDate = dao.creationDate
            item.modificationDate = dao.modificationDate
            item.tags = dao.tags*.tag
        }
        item
    }

    private static Simulation getItem(SimulationRun run) {
        String key = key(SimulationRun, run.id)
        Simulation simulation = itemInstances[key] as Simulation
        if (!simulation) {
            simulation = new Simulation(run.name)
            itemInstances[key] = simulation
        }
        simulation.modelClass = ModellingItemFactory.classLoader.loadClass(run.model)
        simulation.parameterization = getItem(run.parameterization, simulation.modelClass)
        simulation.template = getItem(run.resultConfiguration)
        simulation.creationDate = run.startTime
        simulation.modificationDate = run.modificationDate
        simulation.periodCount = run.periodCount
        simulation.numberOfIterations = run.iterations
        simulation.comment = run.comment
        simulation.creator = run.creator
        simulation.tags = run.tags*.tag
        return simulation
    }

    private static ModelItem getItem(ModelDAO dao) {
        ModelItem item = getItemInstance(key(ModelItem, dao.id))
        if (!item) {
            item = new ModelItem(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            itemInstances[key(ModelItem, dao.id)] = item
        }
        item
    }

    private static ResultConfiguration getItem(ResultConfigurationDAO dao) {
        ResultConfiguration item = getItemInstance(key(ResultConfiguration, dao.id))
        if (!item) {
            item = new ResultConfiguration(dao.name, Thread.currentThread().contextClassLoader.loadClass(dao.modelClassName))
            itemInstances[key(ResultConfiguration, dao.id)] = item
        }
        item.versionNumber = new VersionNumber(dao.itemVersion)
        item.creator = dao.creator
        if (item.creator) {
            item.creator.username = dao.creator.username
        }
        item.lastUpdater = dao.lastUpdater
        if (item.lastUpdater) {
            item.lastUpdater.username = dao.lastUpdater.username
        }
        item.creationDate = dao.creationDate
        item.modificationDate = dao.modificationDate
        item
    }

    private static ModelStructure getItem(ModelStructureDAO dao) {
        ModelStructure item = getItemInstance(key(ModelStructure, dao.id))
        if (!item) {
            item = new ModelStructure(dao.name)
            item.versionNumber = new VersionNumber(dao.itemVersion)
            itemInstances[key(ModelStructure, dao.id)] = item
        }
        item
    }

    static def key(Class itemClass, Long daoId) {
        return "${itemClass?.simpleName}_$daoId".toString()
    }

    static void remove(ModellingItem item) {
        itemInstances.remove(key(item.class, item.id))
    }

    static void clear() {
        itemInstances.clear()
    }

    static ModellingItem updateOrCreateModellingItem(CacheItem source) {
        def key = key(source.itemClass, source.id)
        def target = ModellingItemUpdater.createOrUpdateModellingItem(source, itemInstances[key])
        itemInstances[key] = target
        return target
    }

    static Simulation updateOrCreateModellingItem(SimulationCacheItem source) {
        def key = key(source.itemClass, source.id)
        Simulation target = ModellingItemUpdater.createOrUpdateModellingItem(source, itemInstances[key])
        target.parameterization = source.parameterization ? updateOrCreateModellingItem(source.parameterization) : null
        target.template = source.resultConfiguration ? updateOrCreateModellingItem(source.resultConfiguration) : null
        itemInstances[key] = target
        return target
    }

    static List<ModellingItem> getOrCreateModellingItems(List<CacheItem> itemTOs) {
        itemTOs.collect { CacheItem to ->
            getOrCreateModellingItem(to)
        }
    }

    static ModellingItem getOrCreateModellingItem(CacheItem source) {
        def key = key(source.itemClass, source.id)
        def target = itemInstances[key] ?: ModellingItemUpdater.createOrUpdateModellingItem(source, null)
        itemInstances[key] = target
        target
    }

    static ModellingItem getOrCreateModellingItem(SimulationCacheItem source) {
        def key = key(source.itemClass, source.id)
        Simulation target = itemInstances[key] ?: ModellingItemUpdater.createOrUpdateModellingItem(source, null)
        target.parameterization = source.parameterization ? getOrCreateModellingItem(source.parameterization) : null
        target.template = source.resultConfiguration ? getOrCreateModellingItem(source.resultConfiguration) : null
        itemInstances[key] = target
        target
    }

    static <T extends ModellingItem> T getOrCreate(T source) {
        def key = key(source.class, source.id)
        def object = itemInstances[key]
        if (!object) {
            object = source
            source.load()
            itemInstances[key] = object
        }
        return object
    }


}
