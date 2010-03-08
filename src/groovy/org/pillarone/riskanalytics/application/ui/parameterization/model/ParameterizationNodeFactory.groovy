package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameter.*
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.IntegerParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ConstrainedStringParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.StringParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.DoubleParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.DateParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.BooleanParameterHolder

class ParameterizationNodeFactory {
    static final Logger LOG = Logger.getLogger(ParameterizationNodeFactory)

    public static ParameterizationTableTreeNode getNode(List parameters, Model simulationModel) {
        ParameterHolder param = parameters.find {it != null}
        switch (param.getClass()) {
            case IntegerParameterHolder:
                return createIntegerNode(parameters)
            case DoubleParameterHolder:
                return createDoubleNode(parameters)
            case StringParameterHolder:
                return createStringNode(parameters)
            case ConstrainedStringParameterHolder:
                return createStringNode(parameters, simulationModel)
            case EnumParameterHolder:
                return createEnumNode(parameters)
            case ParameterObjectParameterHolder:
                return createParamaterObjectNode(parameters, simulationModel)
            case MultiDimensionalParameterHolder:
                return createMultiDimensionalParameterNode(parameters, simulationModel)
            case DateParameterHolder:
                return createDateNode(parameters)
            case BooleanParameterHolder:
                return createBooleanNode(parameters)
            default:
                throw new RuntimeException("Unknown paramter type: ${parameters[0].class}")
        }
    }


    private static ParameterizationTableTreeNode createIntegerNode(List parameters) {
        return new IntegerTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createDoubleNode(List parameters) {
        return new DoubleTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createBooleanNode(List parameters) {
        return new BooleanTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createStringNode(List parameters) {
        return new SimpleValueParameterizationTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createStringNode(List parameters, Model model) {
        return new ConstrainedStringParameterizationTableTreeNode(parameters, model)
    }

    private static ParameterizationTableTreeNode createDateNode(List parameters) {
        return new DateParameterizationTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createEnumNode(List parameters) {
        return new EnumParameterizationTableTreeNode(parameters)
    }

    private static ParameterizationTableTreeNode createParamaterObjectNode(List parameters, Model simulationModel) {
        ParameterizationTableTreeNode node = new ParameterObjectParameterTableTreeNode(parameters)

        node.add(new ParameterizationClassifierTableTreeNode(parameters))

        List parameterOrder = []
        for (IParameterObjectClassifier type in parameters?.classifier) {
            if (type != null) {
                parameterOrder.addAll(type.parameterNames)
            }
        }

        Map<String, List<ParameterHolder>> parameterEntries = new TreeMap(new ClassifierComparator(parameterOrder))

        parameters.each {ParameterObjectParameterHolder p ->
            if (p != null) {
                p.classifierParameters.each {Map.Entry entry ->
                    List params = parameterEntries.get(entry.key)
                    if (params == null) {
                        params = new ArrayList()
                        parameters.size().times { params.add(null) }
                        parameterEntries.put(entry.key, params)
                    }
                    params.set(p.periodIndex, entry.value)
                }
            }
        }

        parameterEntries.values().each {List entry ->
            node.add(getNode(entry, simulationModel))
        }
        return node
    }

    private static ParameterizationTableTreeNode createMultiDimensionalParameterNode(List parameters, Model simulationModel) {
        return new MultiDimensionalParameterizationTableTreeNode(parameters, simulationModel)
    }

    public static ITableTreeNode getCompareParameterizationTableTreeNode(Map parametersMap, Model model, int size) {
        if (ParameterizationUtilities.isParameterObjectParameter(parametersMap)) {
            return createCompareParamaterObjectNode(parametersMap, model, size)
        } else {
            List parameters = ParameterizationUtilities.getParameterList(parametersMap)
            ParameterizationTableTreeNode pTTN = getNode(parameters, model)
            CompareParameterizationTableTreeNode cPTTN = new CompareParameterizationTableTreeNode(pTTN, parametersMap, size)
            return cPTTN
        }
    }

    private static ParameterizationTableTreeNode createCompareParamaterObjectNode(Map parametersMap, Model simulationModel, int size) {
        ParameterizationTableTreeNode node = new ParameterObjectParameterTableTreeNode(ParameterizationUtilities.getParameterList(parametersMap))
        Map typeParametersMap = [:]
        try {

            node.add(new CompareParameterizationClassifierTableTreeNode(parametersMap, size))

            List parameterOrder = []
            parametersMap.each {k, parameters ->
                for (IParameterObjectClassifier type in parameters?.classifier) {
                    if (type != null) {
                        parameterOrder.addAll(type.parameterNames)
                    }
                }
            }


            Map<String, List<ParameterHolder>> parameterEntries = new TreeMap(new ClassifierComparator(parameterOrder))
            Map objectsMap = [:]
            parametersMap.each {k, List parameters ->
                parameters.each {ParameterObjectParameterHolder p ->
                    if (p != null) {
                        p.classifierParameters.each {Map.Entry entry ->
                            List params = parameterEntries.get(entry.key)
                            if (params == null) {
                                params = new ArrayList()
                                parameters.size().times { params.add(null) }
                                parameterEntries.put(entry.key, params)
                            }
                            params.set(p.periodIndex, entry.value)

                            Map indexMap = objectsMap.get(entry.key)
                            if (indexMap == null)
                                indexMap = [:]
                            indexMap[k] = params
                            objectsMap[entry.key] = indexMap
                        }
                    }
                }
            }

            objectsMap.each {k, v ->
                node.add(getCompareParameterizationTableTreeNode(v, simulationModel, size))
            }
        } catch (Exception ex) {}

        return node
    }


}


class ClassifierComparator implements Comparator {

    List order

    public ClassifierComparator(List order) {
        this.order = order
    }

    public int compare(Object o1, Object o2) {
        return o1.equals(o2) ? 0 : order.indexOf(o1) < order.indexOf(o2) ? -1 : 1
    }

}
