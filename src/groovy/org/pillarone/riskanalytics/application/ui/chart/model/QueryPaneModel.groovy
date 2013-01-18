package org.pillarone.riskanalytics.application.ui.chart.model

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractPresentationModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

class QueryPaneModel extends AbstractPresentationModel implements ICriteriaModelChangeListener {

    static Log LOG = LogFactory.getLog(QueryPaneModel.class);

    List<List<CriteriaViewModel>> criterias
    SimulationRun simulationRun
    List nodes
    Map longPaths = [:]
    Map fields = [:]
    Map collectors = [:]
    Set results = new HashSet()
    public boolean orderByPath
    boolean enablePeriodComboBox
    List periodLabels = []
    int defaultPeriod = 0

    public QueryPaneModel() {}

    public QueryPaneModel(SimulationRun simulationRun, List nodes, boolean autoQueryOnCreate, boolean enablePeriodComboBox = true, boolean showPeriodLabels = true) {
        this.@enablePeriodComboBox = enablePeriodComboBox
        this.@simulationRun = simulationRun
        this.@nodes = nodes.sort {SimpleTableTreeNode node -> node.path }
        periodLabels = loadPeriodLabels(simulationRun, showPeriodLabels)
        criterias = []
        addCriteriaGroup()
        if (autoQueryOnCreate) {
            query()
        }
    }


    public List<String> getPaths() {
        nodes.path
    }

    public List<String> getDisplayPaths() {
        nodes*.displayPath
    }

    public List<String> getShortPaths() {
        List res = []
        longPaths = [:]
        nodes.each {ResultTableTreeNode node ->
            def shortPath = node.getShortDisplayPath(nodes)
            res << shortPath
            longPaths[shortPath] = node.path
            fields[shortPath] = node.field
            collectors[shortPath] = node.collector
        }
        return res
    }


    public getCriteriaGroupCount() {
        return criterias.size()
    }

    public List<CriteriaViewModel> getCriteriaGroup(int index) {
        criterias[index]
    }

    public addCriteriaGroup() {
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(this, enablePeriodComboBox)
        criteriaViewModel.addListener(this)
        criterias << [criteriaViewModel]
        criteriaChanged()
        fireModelChanged()
    }

    public removeCriteriaGroup(int index) {
        List<CriteriaViewModel> modelsToRemove = criterias.remove(index)
        for(CriteriaViewModel model in modelsToRemove) {
            model.removeListener(this)
        }
        criteriaChanged()
        fireModelChanged()
    }

    public addCriteria(int groupIndex) {
        CriteriaViewModel criteriaViewModel = new CriteriaViewModel(this, enablePeriodComboBox)
        criteriaViewModel.addListener(this)
        criterias[groupIndex] << criteriaViewModel
        criteriaChanged()
        fireModelChanged()
    }

    public void removeCriteria(CriteriaViewModel criteria) {
        criteria.removeListener(this)
        Integer groupIndexToRemove = null
        criterias.eachWithIndex {List<CriteriaViewModel> criteriaGroup, int index ->
            criteriaGroup.remove(criteria)
            if (criteriaGroup.size() == 0) {
                groupIndexToRemove = index
            }
        }
        if (groupIndexToRemove != null) {
            removeCriteriaGroup(groupIndexToRemove)
        }
        criteriaChanged()
        fireModelChanged()
    }

    public void query() {
        results = new HashSet()

        if (criterias.empty) {
            simulationRun.iterations.times {
                results << it + 1
            }
            return
        }

        //MySQL 5.1 can not handle self joins with more than 8 table references
        //so the AND and OR criterias are implemented programmatic
        criterias.each {List group ->
            List groupResult = []
            int size = Integer.MAX_VALUE
            List smallesList = null
            group.eachWithIndex {CriteriaViewModel criteria, int i ->
                List criteriaResult = queryResultsHQL(criteria)
                if (criteriaResult.size() < size) {
                    size = criteriaResult.size()
                    if (smallesList != null) {
                        groupResult << smallesList
                    }
                    smallesList = criteriaResult
                } else {
                    groupResult << criteriaResult
                }

            }
            if (smallesList != null) {
                //programmatic or with a set
                results.addAll(programmaticAnd(groupResult, smallesList))
            }
        }

        fireModelChanged()
    }

    public boolean validate() {
        boolean isValid = true
        for (List group: criterias) {
            for (CriteriaViewModel criteria: group) {
                isValid = isValid && criteria.validate()
            }
        }
        return isValid
    }

    public String getErrorMessage() {
        for (List group: criterias) {
            for (CriteriaViewModel criteria: group) {
                if (!criteria.validate()) {
                    return criteria.getErrorMessage()
                }
            }
        }
        return null
    }

    private List programmaticAnd(List groupList, List smallesList) {
        if (groupList.size() == 0) {
            return smallesList
        } else {
            List res = []
            smallesList.each {int iteration ->
                int groupIndex = 0
                List list = groupList[groupIndex]
                boolean isInAll = list.contains(iteration)

                while (isInAll && groupIndex < groupList.size() - 1) {
                    groupIndex++
                    list = groupList[groupIndex]
                    isInAll = isInAll && list.contains(iteration)
                }

                if (isInAll) {
                    res << iteration
                }
            }
            return res
        }
    }

    public void setDefaultPeriod(int period) {
        this.@defaultPeriod = period
        criterias.each {List it ->
            it.each {CriteriaViewModel model ->
                model.selectedPeriod = defaultPeriod
            }
        }
    }

    public String getPeriodLabel(int periodIndex) {
        return periodLabels[periodIndex]
    }

    public int getPeriodNumber(String periodName) {
        return periodLabels.findIndexOf { it == periodName}
    }


    protected List clearCriteriaList(String path, int period) {
        List ret = []
        criterias.each {List oldGroup ->
            List newGroup = []
            oldGroup.each {CriteriaViewModel model ->
                if (model.selectedPath == path && model.selectedPeriod == period) {
                    newGroup << model
                }
            }
            if (newGroup.size() > 0) {
                ret << newGroup
            }
        }
        return ret
    }

    protected List queryResultsHQL(CriteriaViewModel criteria) {
        return ResultAccessor.getCriteriaConstrainedIterations(simulationRun, criteria.selectedPeriod, criteria.selectedPath,
                criteria.field, criteria.collector, criteria.selectedComparator.toString(), criteria.interpretedValue);
    }

    protected String createCriteriaSubQuerry(CriteriaViewModel model) {
        try {
            return "sum(s.value) " + model.selectedComparator.toString() + " " + model.interpretedValue
        } catch (Exception ex) {
            return null
        }
    }

    public List createResultList() {
        List lastQueryResults = []
        List<Map<Integer, Double>> resultsPerPathAndPeriod = []
        List<Integer> iterations = new ArrayList(results)
        if (iterations.size() == 0) {
            return []
        }
        iterations = iterations.sort()

        if (orderByPath) {
            for (ResultTableTreeNode node in nodes) {
                for (int period = 0; period < simulationRun.periodCount; period++) {
                    addResultsPerPathAndPeriod(node, period, resultsPerPathAndPeriod, iterations)
                }
            }
        } else {
            for (int period = 0; period < simulationRun.periodCount; period++) {
                for (ResultTableTreeNode node in nodes) {
                    addResultsPerPathAndPeriod(node, period, resultsPerPathAndPeriod, iterations)
                }
            }
        }

        for (Integer iterationNumber in iterations) {
            List row = []
            row << iterationNumber
            for (Map<Integer, Double> pathResults in resultsPerPathAndPeriod) {
                row << pathResults[iterationNumber]
            }
            lastQueryResults << row
        }
        return lastQueryResults
    }

    private void addResultsPerPathAndPeriod(ResultTableTreeNode node, int period, List resultsPerPathAndPeriod, List<Integer> iterations) {
        String path = node.path
        String field = node.field
        resultsPerPathAndPeriod << ResultAccessor.getIterationConstrainedValues(simulationRun, period, path, field, node.collector, iterations)
    }

    void criteriaChanged() {
    }
}