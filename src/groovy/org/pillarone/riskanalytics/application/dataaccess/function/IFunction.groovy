package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun

import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

interface IFunction extends Cloneable {

    String getName(int periodIndex)

    def evaluate(SimulationRun simulationRun, int periodIndex, SimpleTableTreeNode node)
}

/** A Function for Result views       */
abstract class ResultFunction implements IFunction {
    String i18nName

    static String getPath(SimpleTableTreeNode node) {
        if (node.path) {
            node.path
        }
        else if (node.parent?.name) {
            "${node.parent.name}"
        }
    }

    String getName(int periodIndex) {
        return getName()
    }

    abstract String getName()

    abstract String getKeyFigureName()

    BigDecimal getKeyFigureParameter() {
        return null
    }

    static String getAttributeName(SimpleTableTreeNode node) {
        node.path.substring(node.path.lastIndexOf(":") + 1)
    }

    def evaluate(SimulationRun simulationRun, int periodIndex, SimpleTableTreeNode node) {
        null
    }

    boolean calculateForNonStochasticalValues() {
        false;
    }

    public ResultFunction clone() {
        return (ResultFunction) super.clone();
    }


}

class NodeNameFunction implements IFunction {

    final String name = "Name"

    def evaluate(SimulationRun simulationRun, int periodIndex, SimpleTableTreeNode node) {
        node.displayName
    }

    public String getName(int periodIndex) {
        return name
    }

    boolean calculateForNonStochasticalValues() {
        true;
    }
}



class Mean extends ResultFunction {

    final String name = "Mean"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getMean(simulationRun, periodIndex, getPath(node), node.collector, node.field)
    }

    boolean calculateForNonStochasticalValues() {
        true;
    }

    public String getKeyFigureName() {
        PostSimulationCalculation.MEAN
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }

    public boolean equals(Object obj) {
        return obj && getName().equals(obj.getName())
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(name)
        return hcb.toHashCode();
    }


}

class Min extends ResultFunction {

    final String name = "Min"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getMin(simulationRun, periodIndex, getPath(node), node.collector, node.field)
    }

    public String getKeyFigureName() {
        'min'
    }
}
class Max extends ResultFunction {

    final String name = "Max"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getMax(simulationRun, periodIndex, getPath(node), node.collector, node.field)
    }

    public String getKeyFigureName() {
        'max'
    }
}
class Sigma extends ResultFunction {

    final String name = "Sigma"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getStdDev(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field)
    }

    public String getKeyFigureName() {
        PostSimulationCalculation.STDEV
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}

class AbsoluteValue extends ResultFunction {

    final String name = "AbsoluteValue"


    public String getKeyFigureName() {
        return 'min'
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}

abstract class CompareFunction extends ResultFunction {
    final double MIN_VALUE = 0.000001

    IFunction underlyingFunction
    SimulationRun runA
    SimulationRun runB

}


class DeviationPercentage extends CompareFunction {

    final String name = "DevPercentage"


    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node, Double aValue, Double bValue) {
        aValue = aValue ? aValue : underlyingFunction.evaluate(runA, periodIndex, node)
        bValue = bValue ? bValue : underlyingFunction.evaluate(runB, periodIndex, node)
        if (aValue == null || bValue == null || aValue == 0 || bValue < MIN_VALUE || aValue < MIN_VALUE)
            return null

        return ((bValue - aValue) / aValue) * 100
    }

    public String getKeyFigureName() {
        return 'percentage'
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}

class DeviationAbsoluteDifference extends CompareFunction {

    final String name = "DevAbsoluteDifference"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node, Double aValue, Double bValue) {
        aValue = aValue ? aValue : underlyingFunction.evaluate(runA, periodIndex, node)
        bValue = bValue ? bValue : underlyingFunction.evaluate(runB, periodIndex, node)
        if (aValue == null || bValue == null)
            return null

        return bValue - aValue
    }

    public String getKeyFigureName() {
        return 'absoluteDifference'
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}

class FractionPercentage extends CompareFunction {

    final String name = "FrPercentage"


    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node, Double aValue, Double bValue) {
        aValue = aValue ? aValue : underlyingFunction.evaluate(runA, periodIndex, node)
        bValue = bValue ? bValue : underlyingFunction.evaluate(runB, periodIndex, node)
        if (aValue == null || bValue == null || aValue == 0 || bValue < MIN_VALUE || aValue < MIN_VALUE)
            return null

        return (bValue / aValue) * 100
    }

    public String getKeyFigureName() {
        return 'percentage'
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}

class FractionAbsoluteDifference extends CompareFunction {

    final String name = "FrAbsoluteDifference"

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node, Double aValue, Double bValue) {
        aValue = aValue ? aValue : underlyingFunction.evaluate(runA, periodIndex, node)
        bValue = bValue ? bValue : underlyingFunction.evaluate(runB, periodIndex, node)
        if (aValue == null || bValue == null || aValue == 0 || bValue < MIN_VALUE || aValue < MIN_VALUE)
            return null

        return (bValue - aValue) / aValue
    }

    public String getKeyFigureName() {
        return 'absoluteDifference'
    }

    public String getName() {
        if (i18nName == null) {
            return name
        } else {
            return i18nName
        }
    }
}



class ColumnOrder extends ResultFunction {
    ColumnOrderType orderType

    public ColumnOrder(orderType) {
        this.orderType = orderType
    }

    public String getKeyFigureName() {
        return "columnOrder";
    }

    public String getName() {
        if (i18nName == null) {
            switch (orderType) {
                case ColumnOrderType.byPeriod: return "ColumnOrderByPeriod"
                case ColumnOrderType.byKeyFigure: return "ColumnOrderByKeyFigure"
            }
        } else {
            return i18nName
        }
    }

}


class Percentile extends ResultFunction {

    String name
    final Double percentile

    public void setPercentile(Double percentile) {
        name = percentile + " " + i18nName
        this.@percentile = percentile
    }

    def evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getPercentile(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field, percentile)
    }

    public String getKeyFigureName() {
        PostSimulationCalculation.PERCENTILE
    }

    BigDecimal getKeyFigureParameter() {
        return percentile
    }
}

class Var extends ResultFunction {

    Double percentile

    public Var(Double percentile) {
        this.@percentile = percentile
    }

    public String getName() {
        return "$percentile VaR"
    }

    public double evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getVar(simulationRun, periodIndex, getPath(node), node.collector, node.field, percentile)
    }

    public String getKeyFigureName() {
        PostSimulationCalculation.VAR
    }

    BigDecimal getKeyFigureParameter() {
        return percentile
    }

}

class SingleIteration extends ResultFunction {

    int iteration

    public SingleIteration(int iteration) {
        this.@iteration = iteration
    }

    public String getName() {
        return "$iteration. Iteration"
    }

    public Double evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getUltimatesForOneIteration(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field, iteration)
    }

    boolean calculateForNonStochasticalValues() {
        true;
    }

    public String getKeyFigureName() {
        'singleIteration'
    }
}

class Tvar extends ResultFunction {

    Double percentile

    public Tvar(Double percentile) {
        this.@percentile = percentile
    }

    public String getName() {
        return "$percentile TVaR"
    }

    public evaluate(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        ResultAccessor.getTvar(simulationRun, periodIndex, getPath(node), node.collector, node.field, percentile)
    }

    public String getKeyFigureName() {
        PostSimulationCalculation.TVAR
    }

    BigDecimal getKeyFigureParameter() {
        return percentile
    }

}

enum ColumnOrderType {
    byPeriod, byKeyFigure
}
