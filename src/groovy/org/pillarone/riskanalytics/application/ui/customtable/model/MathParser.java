package org.pillarone.riskanalytics.application.ui.customtable.model;


import com.ulcjava.base.client.UIList;
import org.apache.poi.ss.util.NumberComparer;
import org.nfunk.jep.Node;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.JEP;
import org.pillarone.riskanalytics.application.ui.customtable.GroovyHelperMethods;
import org.pillarone.riskanalytics.application.ui.customtable.JavaHelperMethods;
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess;
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor;
import org.pillarone.riskanalytics.core.dataaccess.ResultPathDescriptor;
import org.pillarone.riskanalytics.core.output.*;

import java.util.*;

/**
 *
 * @author ivo.nussbaumer
 */
public class MathParser implements IMathParser {

    private final JEP jep;

    /**
     * Constructor
     */
    public MathParser () {
        jep = new JEP();
        jep.addStandardFunctions();

        jep.addFunction("mean", new Mean());
        jep.addFunction("min", new Min());
        jep.addFunction("max", new Max());
        jep.addFunction("pOneMean", new P1MeanFunction());
        jep.addFunction("pOnePercentile", new P1PercentileFunction());
        jep.addFunction("pOneMax", new P1MaxFunction());
        jep.addFunction("pOneMin", new P1MinFunction());
        jep.addFunction("pOneStdDev", new P1StdDev());
        jep.addFunction("pOneSingleIteration", new P1IterationData());
        jep.addFunction("pOneProbExceedance", new P1ProbExceedanceFunction());
//        jep.addFunction("median", new Median());
//        jep.addFunction("stddev", new StdDev());
    }

    public Node parseExpression(String formula) {
        return jep.parseExpression(formula);
    }

    public double getValue() throws ParseException {
        Double value = jep.getValue();
        if(jep.hasError()) {
            throw new ParseException(jep.getErrorInfo());
        }
        if(((Double) jep.getValue()).isInfinite() || ((Double) jep.getValue()).isNaN() ) {
            throw new ParseException("NaN");
        }

        return value;
    }

    /**
     * Mean Function for the MathParser JEP
     */
    private class Mean extends PostfixMathCommand {
        public Mean () {
            // set numberOfParameters to -1 -> variable number of parameters
            numberOfParameters = -1;
        }



        public void run(Stack stack) throws ParseException {
            checkStack(stack); // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Mean");

            double sum = 0.0;
            int numValues = 0;
            Object value;

            // get the parameters from the stack, and calc the sum
            while (!stack.empty()) {
                value = stack.pop();
                if (value instanceof Number) {
                    sum += ((Number) value).doubleValue();
                    numValues++;
                }
            }

            // calc the mean
            double mean = sum / numValues;

            // push the result back on the now empty stack

            stack.push(mean);
        }
    }

    /**
     * Mean Function for the MathParser JEP
     */
    private class P1MeanFunction extends PostfixMathCommand {
        public P1MeanFunction () {
            numberOfParameters = 5;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            if(stringBuilder.toString().length() > 0) {
                throw new ParseException(stringBuilder.toString());
            }
            double result = ResultAccessor.getMean(simulationRun, period.intValue() - 1, pathName, collectorName, fieldName);
            stack.push(result);
        }
    }

    private class P1PercentileFunction extends PostfixMathCommand {
        public P1PercentileFunction () {
            numberOfParameters = 6;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            Number percentile = (Number) stack.pop();
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double result = ResultAccessor.getPercentile(simulationRun, period.intValue() - 1, pathName, collectorName, fieldName, percentile.doubleValue(), QuantilePerspective.LOSS);
            stack.push(result);
        }
    }

    private class P1ProbExceedanceFunction extends PostfixMathCommand {
        public P1ProbExceedanceFunction () {
            numberOfParameters = 6;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            Number threshold = (Number) stack.pop();
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double valuesAboveThreshold = GroovyHelperMethods.valuesAboveThreshold(simulationRun, period.intValue(), pathName, collectorName, fieldName, threshold.doubleValue());
            double numberSimulations = (double) simulationRun.getIterations();
            double result = valuesAboveThreshold / numberSimulations;
            stack.push(result);
        }
    }

    private class P1IterationData extends PostfixMathCommand {
        public P1IterationData () {
            numberOfParameters = 6;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            Number iteration = (Number) stack.pop();
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double result = ResultAccessor.getSingleIterationValue(simulationRun, period.intValue() - 1, pathName, fieldName,  collectorName, iteration.intValue());
            stack.push(result);
        }
    }


    private class P1MaxFunction extends PostfixMathCommand {
        public P1MaxFunction () {
            numberOfParameters = 5;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double result = ResultAccessor.getMax(simulationRun, period.intValue() - 1, pathName, collectorName, fieldName);
            stack.push(result);
        }
    }

    private class P1MinFunction extends PostfixMathCommand {
        public P1MinFunction () {
            numberOfParameters = 5;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double result = ResultAccessor.getMin(simulationRun, period.intValue() - 1, pathName, collectorName, fieldName);
            stack.push(result);
        }
    }

    private class P1StdDev extends PostfixMathCommand {
        public P1StdDev () {
            numberOfParameters = 5;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String collectorName = (String) stack.pop();
            Number period = (Number) stack.pop();
            String fieldName = (String) stack.pop();
            String pathName = (String) stack.pop();
            String simRunName = (String) stack.pop();

            StringBuilder stringBuilder = new StringBuilder();
            SimulationRun simulationRun = GroovyHelperMethods.findSimulationRun(simRunName);
            JavaHelperMethods.checkSimulationRun(stringBuilder, simulationRun, simRunName);
            JavaHelperMethods.checkPeriod(period, stringBuilder);
            JavaHelperMethods.checkModelContextInfo(period, stringBuilder, simulationRun, pathName, fieldName, collectorName);

            double result = ResultAccessor.getStdDev(simulationRun, period.intValue() - 1, pathName, collectorName, fieldName);
            stack.push(result);
        }
    }


    private class Min extends PostfixMathCommand {
        public Min () {
            numberOfParameters = -1;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack); // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Min");

            double minValue = Double.MAX_VALUE;
            Object value;

            while (!stack.empty()) {
                value = stack.pop();
                if (value instanceof Number && ((Number) value).doubleValue()  < minValue) {
                    minValue = ((Number) value).doubleValue();
                }
            }
            stack.push(minValue);
        }
    }

    private class Max extends PostfixMathCommand {
        public Max () {
            numberOfParameters = -1;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack); // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Max");

            double maxValue = Double.MIN_VALUE;
            Object value;

            while (!stack.empty()) {
                value = stack.pop();
                if (value instanceof Number && ((Number) value).doubleValue()  > maxValue) {
                    maxValue = ((Number) value).doubleValue();
                }
            }
            stack.push(maxValue);
        }
    }

/*    private class Median extends PostfixMathCommand {
        public Median () {
            numberOfParameters = -1;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack); // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Median");

            List<Number> params = new LinkedList<Number>();
            Object value;

            while (!stack.empty()) {
                value = stack.pop();
                if (value instanceof Number) {
                    params.add(((Number) value));
                }
            }

            Collections.sort(params, new Comparator<Number>() {
                public int compare(Number o1, Number o2) {
                    Double d1= (o1 == null) ? Double.POSITIVE_INFINITY : o1.doubleValue();
                    Double d2= (o2 == null) ? Double.POSITIVE_INFINITY : o2.doubleValue();
                    return  d1.compareTo(d2);
                }
            });
            double median;
            Number[] numbers = Number[] params.toArray();
            if (params.size() % 2 == 0) {
                median = ((params.toArray()[(int) (params.size() / 2 - 1)]) + params.toArray()[(int) (params.size() / 2)]) / 2;

            } else {
                median = params.toArray()[(int) (params.size() / 2)];
            }
            stack.push(median);
        }
    }*/

/*    private class StdDev extends PostfixMathCommand {
        public StdDev () {
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for StdDev")

            // get parameters
            List params = new LinkedList<Number>()
            Object value
            while (!stack.empty()) {
                value = stack.pop()
                if (value instanceof Number) {
                    params.add(value)
                }
            }

            // calc mean
            double sum = 0.0
            for (Number n : params)
                sum += n
            double mean = sum / params.size()

            // calc sum of variances
            sum = 0.0
            for (Number n : params) {
                double v = n - mean;
                sum += v * v;
            }

            // calc standard deviation
            double stdDev = Math.sqrt (sum / params.size())
            stack.push(stdDev)
        }
    }*/
}