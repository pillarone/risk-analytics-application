package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.item.ParameterNotFoundException
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.*

class ParameterizationNodeFactory {
    static final Logger LOG = Logger.getLogger(ParameterizationNodeFactory)

    public static ParameterizationTableTreeNode getNode(String path, ParametrizedItem item, Model simulationModel) {
        ParameterHolder param = item.getParameterHoldersForFirstPeriod(path)
        switch (param.class) {
            case IntegerParameterHolder:
                return createIntegerNode(path, item)
            case DoubleParameterHolder:
                return createDoubleNode(path, item)
            case StringParameterHolder:
                return createStringNode(path, item)
            case ConstrainedStringParameterHolder:
                return createStringNode(path, item, simulationModel)
            case EnumParameterHolder:
                return createEnumNode(path, item)
            case ParameterObjectParameterHolder:
                return createParamaterObjectNode(path, item, simulationModel)
            case MultiDimensionalParameterHolder:
                return createMultiDimensionalParameterNode(path, item, simulationModel)
            case DateParameterHolder:
                return createDateNode(path, item)
            case BooleanParameterHolder:
                return createBooleanNode(path, item)
            case ResourceParameterHolder:
                return createResourceNode(path, item)
            case DataSourceParameterHolder:
                return createDataSourceNode(path, item)
            default:
                throw new RuntimeException("Unknown paramter type: ${param?.class}")
        }
    }


    private static ParameterizationTableTreeNode createIntegerNode(String path, ParametrizedItem item) {
        return new IntegerTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createDoubleNode(String path, ParametrizedItem item) {
        return new DoubleTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createBooleanNode(String path, ParametrizedItem item) {
        return new BooleanTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createStringNode(String path, ParametrizedItem item) {
        return new SimpleValueParameterizationTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createResourceNode(String path, ParametrizedItem item) {
        return new ResourceParameterizationTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createStringNode(String path, ParametrizedItem item, Model model) {
        return new ConstrainedStringParameterizationTableTreeNode(path, item, model)
    }

    private static ParameterizationTableTreeNode createDateNode(String path, ParametrizedItem item) {
        return new DateParameterizationTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createDataSourceNode(String path, ParametrizedItem item) {
        return new ResultDataParameterizationTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createEnumNode(String path, ParametrizedItem item) {
        return new EnumParameterizationTableTreeNode(path, item)
    }

    private static ParameterizationTableTreeNode createParamaterObjectNode(String path, ParametrizedItem item, Model simulationModel) {
        ParameterizationTableTreeNode node = new ParameterObjectParameterTableTreeNode(path, item)

        node.add(new ParameterizationClassifierTableTreeNode(path, item, simulationModel))

        List parameterOrder = []
        List<ParameterHolder> parameterHolders = item.getParameterHoldersForAllPeriods(path)
        for (IParameterObjectClassifier type in parameterHolders*.classifier) {
            if (type != null) {
                parameterOrder.addAll(type.parameterNames)
            }
        }

        Map<StringClassKey, String> parameterEntries = new TreeMap(new ClassifierComparator(parameterOrder))

        parameterHolders.each {ParameterObjectParameterHolder p ->
            if (p != null) {
                p.classifierParameters.each {Map.Entry<String, ParameterHolder> entry ->
                    final StringClassKey key = new StringClassKey(name: entry.key, clazz: entry.value.class)
                    if (!parameterEntries.containsKey(key)) {
                        parameterEntries[key] = entry.key
                    }
                }
            }
        }

        parameterEntries.values().each {String entry ->
            node.add(getNode("${path}:${entry}", item, simulationModel)) //TODO: same name / different tyoe doesn't work anymore
        }
        return node
    }

    private static ParameterizationTableTreeNode createMultiDimensionalParameterNode(String path, ParametrizedItem item, Model simulationModel) {
        return new MultiDimensionalParameterizationTableTreeNode(path, item, simulationModel)
    }

    public static ITableTreeNode getCompareParameterizationTableTreeNode(String path, List<ParametrizedItem> items, Model model, int size) {
        if (ParameterizationUtilities.isParameterObjectParameter(path, items)) {
            return createCompareParamaterObjectNode(path, items, size, model)
        } else {
            return new CompareParameterizationTableTreeNode(path, items, size, model)
        }
    }

    private static ParameterizationTableTreeNode createCompareParamaterObjectNode(String path, List<ParametrizedItem> items, int size, Model model) {
        ParameterizationTableTreeNode node = new ParameterObjectParameterTableTreeNode(path, items[0])
        node.add(new CompareParameterizationClassifierTableTreeNode(path, items, size, model))

        List parameterOrder = []
        for (ParametrizedItem item in items) {
            try {
                List<ParameterHolder> parameterHolders = item.getParameterHoldersForAllPeriods(path)
                for (IParameterObjectClassifier type in parameterHolders*.classifier) {
                    if (type != null) {
                        parameterOrder.addAll(type.parameterNames)
                    }
                }
            } catch (ParameterNotFoundException e) {
                //this parameter does not exist in all the compared parameterizations
            }
        }

        Map<StringClassKey, String> parameterEntries = new TreeMap(new ClassifierComparator(parameterOrder))

        for (ParametrizedItem item in items) {
            try {
                List<ParameterHolder> parameterHolders = item.getParameterHoldersForAllPeriods(path)
                parameterHolders.each {ParameterObjectParameterHolder p ->
                    if (p != null) {
                        p.classifierParameters.each {Map.Entry<String, ParameterHolder> entry ->
                            final StringClassKey key = new StringClassKey(name: entry.key, clazz: entry.value.class)
                            if (!parameterEntries.containsKey(key)) {
                                parameterEntries.put(key, entry.key)
                            }
                        }
                    }
                }
            } catch (ParameterNotFoundException e) {
                //this parameter does not exist in all the compared parameterizations
            }
        }

        parameterEntries.values().each {String entry ->
            node.add(getCompareParameterizationTableTreeNode("${path}:${entry}", items, model, size)) //TODO: same name / different tyoe doesn't work anymore
        }

        return node
    }

    static class StringClassKey {

        String name
        Class clazz

        @Override
        int hashCode() {
            return new HashCodeBuilder().append(name).append(clazz).toHashCode()
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof StringClassKey) {
                return new EqualsBuilder().append(name, obj.name).append(clazz, obj.clazz).equals
            }
            return false
        }


    }

}


class ClassifierComparator<StringClassKey> implements Comparator<StringClassKey> {

    List order

    public ClassifierComparator(List order) {
        this.order = order
    }

    public int compare(StringClassKey o1, StringClassKey o2) {
        return o1.equals(o2) ? 0 : order.indexOf(o1.name) < order.indexOf(o2.name) ? -1 : 1
    }

}
