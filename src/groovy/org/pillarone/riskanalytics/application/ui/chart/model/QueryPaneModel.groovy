package org.pillarone.riskanalytics.application.ui.chart.model

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractPresentationModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult

class QueryPaneModel extends AbstractPresentationModel {

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
        criterias = [[new CriteriaViewModel(this, enablePeriodComboBox)]]
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
        criterias << [new CriteriaViewModel(this, enablePeriodComboBox)]
        fireModelChanged()
    }

    public removeCriteriaGroup(int index) {
        criterias.remove(index)
        fireModelChanged()
    }

    public addCriteria(int groupIndex) {
        criterias[groupIndex] << new CriteriaViewModel(this, enablePeriodComboBox)
        fireModelChanged()
    }

    public void removeCriteria(CriteriaViewModel criteria) {
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
        try {
            String q = "SELECT s.iteration " +
                    "FROM org.pillarone.riskanalytics.core.output.SingleValueResult as s " +
                    "WHERE s.simulationRun.id = " + simulationRun.id +
                    " AND s.period = " + criteria.selectedPeriod +
                    " AND s.path.pathName = '" + criteria.selectedPath + "'" +
                    " AND s.field.fieldName = '" + criteria.field + "'" +
                    " AND s.value " + criteria.selectedComparator.toString() + " " + criteria.interpretedValue
            LOG.debug "Query: " + q
            return SingleValueResult.executeQuery(q)
        } catch (Exception ex) {
            return []
        }
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
        List resultsPerPathAndPeriod = []
        List iterations = new ArrayList(results)
        if (iterations.size() == 0) {
            return []
        }
        iterations = iterations.sort()

        List<List<Integer>> splitUpIterations = getSplitUpIterations(iterations)

        if (orderByPath) {
            nodes.each {ResultTableTreeNode node ->
                simulationRun.periodCount.times {int period ->
                    addResultsPerPathAndPeriod(node, period, resultsPerPathAndPeriod, splitUpIterations)
                }
            }
        } else {
            simulationRun.periodCount.times {int period ->
                nodes.each {ResultTableTreeNode node ->
                    addResultsPerPathAndPeriod(node, period, resultsPerPathAndPeriod, splitUpIterations)
                }
            }
        }

        iterations.eachWithIndex {int iterationNumber, int index ->
            List row = []
            row << iterationNumber
            resultsPerPathAndPeriod.each {List pathResults ->
                row << pathResults[index]
            }
            lastQueryResults << row
        }

        return lastQueryResults
    }

    private void addResultsPerPathAndPeriod(ResultTableTreeNode node, int period, List resultsPerPathAndPeriod, List<List<Integer>> splitUpIterations) {
        String path = node.path
        String field = node.field
        List periodList = []

        for (List<Integer> list in splitUpIterations) {
            StringBuilder query = new StringBuilder("SELECT  sum(s.value) ")
            query.append("FROM org.pillarone.riskanalytics.core.output.SingleValueResult as s ")
            query.append("WHERE s.simulationRun.id = " + simulationRun.id)
            query.append(" AND s.period = " + period)
            query.append(" AND s.path.pathName = '" + path + "'")
            query.append(" AND s.field.fieldName = '" + field + "'")
            query.append(" AND s.iteration in (:list) GROUP BY s.iteration ORDER BY s.iteration asc")
            periodList.addAll(SingleValueResult.executeQuery(query.toString(), ["list": list]))
        }
        resultsPerPathAndPeriod << periodList
    }

    List getSplitUpIterations(List iterations) {
        //do not use more than 1000 ids in the in clause
        List<List<Integer>> splitUpIterations = []
        if (iterations.size() > 1000) {
            int i = 0
            while (i < iterations.size()) {
                int lowerBound = i
                i += 1000
                int upperBound = Math.min(i - 1, iterations.size() - 1)
                splitUpIterations << iterations[lowerBound..upperBound]
            }
        } else {
            splitUpIterations << iterations
        }
        return splitUpIterations
    }


}