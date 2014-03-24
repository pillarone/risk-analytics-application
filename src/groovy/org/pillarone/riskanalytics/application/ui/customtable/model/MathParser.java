package org.pillarone.riskanalytics.application.ui.customtable.model;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.pillarone.riskanalytics.application.ui.customtable.GroovyHelperMethods;
import org.pillarone.riskanalytics.application.ui.customtable.JavaHelperMethods;
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor;
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry;
import org.pillarone.riskanalytics.core.output.QuantilePerspective;
import org.pillarone.riskanalytics.core.output.SimulationRun;
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.item.Parameterization;
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber;
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder;
import org.pillarone.riskanalytics.core.simulation.item.parameter.StringParameterHolder;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author simon . parten @ art - allianz . com
 */
public class MathParser implements IMathParser {

    private final JEP jep;

    public static final DateTimeFormatter formatDate = DateTimeFormat.forPattern("dd-MMMM-yyyy");

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
        jep.addFunction("pOneParameter", new P1ParameterFunction());
        jep.addFunction("pOneParameterStrategy", new P1ParameterStrategyFunction());
        jep.addFunction("pOneParameterStrategyType", new P1ParameterStrategyTypeFunction());
        jep.addFunction("pOneParameterTableValue", new P1ParameterTableFunction());
//        jep.addFunction("median", new Median());
//        jep.addFunction("stddev", new StdDev());
    }

    public Node parseExpression(String formula) {
        return jep.parseExpression(formula);
    }

    public Object getValue() throws ParseException {
        Object value = jep.getValueAsObject();
        if(jep.hasError()) {
            throw new ParseException(jep.getErrorInfo());
        }
        if(value instanceof Double) {
            if(((Double) jep.getValue()).isInfinite() || ((Double) jep.getValue()).isNaN() ) {
                throw new ParseException("NaN");
            }
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

    private class P1ParameterFunction extends PostfixMathCommand {
        public P1ParameterFunction() {
            numberOfParameters = 4;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String modelPath = (String) stack.pop();
            String versionNumber = (String) stack.pop();
            String paramName = (String) stack.pop();
            String modelName = (String) stack.pop();

            Object parameterValue = getModelParameterObject(modelPath, versionNumber, paramName, modelName);
            if(parameterCanBeDisplayed(parameterValue)){
                stack.push(parameterValue.toString());
                return;
            }
            throw new ParseException("Parameter at path " + modelPath +" is of type: " + parameterValue.toString() + ". Not compatible with this function. Do you want the pOneStrategyParameter function?");
        }
    }


    private Object getModelParameterObject(String modelPath, String versionNumber, String paramName, String modelName) throws ParseException {
        Class modelClass = null;
        try {
            modelClass = ModelRegistry.getInstance().getModelClass(modelName);
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Failed to find model class for " + modelName  + ". Have you included the namespace? Example; models.gira.GIRAModel");
        }

        Parameterization parameterization = new Parameterization(paramName, modelClass);
        parameterization.setVersionNumber(new VersionNumber(versionNumber));
        parameterization.load();
        List<ParameterHolder> parameterHolders = parameterization.getParameterHolders();
        ParameterHolder selectedHolder = new StringParameterHolder("", 0, "");
        boolean foundParameter = false;
        for (ParameterHolder parameterHolder : parameterHolders) {
            if(parameterHolder.getPath().equals(modelPath)) {
                selectedHolder = parameterHolder;
                foundParameter = true;
                break;
            }
        }
        if(!foundParameter) {
            throw new ParseException("Failed to find parameter at path : " + modelPath);
        }
//        if(selectedHolder  instanceof MultiDimensionalParameterHolder){
//            throw new ParseException("Cannot parse : " + modelPath);
//        }
        return selectedHolder.getBusinessObject();
    }

    private class P1ParameterStrategyFunction extends PostfixMathCommand {
        public P1ParameterStrategyFunction() {
            numberOfParameters = 5;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String fieldValue = (String) stack.pop();
            String modelPath = (String) stack.pop();
            String versionNumber = (String) stack.pop();
            String paramName = (String) stack.pop();
            String modelName = (String) stack.pop();

            Object parameterValue = getModelParameterObject(modelPath, versionNumber, paramName, modelName);
            if( !(parameterValue instanceof IParameterObject)){
                throw new ParseException("Parameter at path " + modelPath +" is of type: " + parameterValue.getClass() + ". Not compatible with this function. Do you want the pOneParameter function?");
            }
            Map<String, Object> someParameters = ((Map<String, Object>) ((IParameterObject) parameterValue).getParameters());
            Object aParameter = someParameters.get(fieldValue);
            if( aParameter == null  ) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Map.Entry<String, Object> anEntry : someParameters.entrySet()) {
                    stringBuilder.append(anEntry.getKey());
                    stringBuilder.append(" ,");
                }
                throw new ParseException("Path found. Field : " + fieldValue + " not found at path. Possible values;" + stringBuilder.toString());
            }
            if(parameterCanBeDisplayed(aParameter)){
                stack.push(guiFormat(aParameter));
                return;
            }
            throw new ParseException("Parameter " + aParameter.toString() + " cannot be displayed. Did you want a different parameter reporting function?");

        }
    }

    private class P1ParameterStrategyTypeFunction extends PostfixMathCommand {
        public P1ParameterStrategyTypeFunction() {
            numberOfParameters = 4;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            String modelPath = (String) stack.pop();
            String versionNumber = (String) stack.pop();
            String paramName = (String) stack.pop();
            String modelName = (String) stack.pop();

            Object parameterValue = getModelParameterObject(modelPath, versionNumber, paramName, modelName);
            if( (! (parameterValue instanceof IParameterObject))){
                throw new ParseException("Parameter at path " + modelPath +" is of type: " + parameterValue.getClass() + ". Not compatible with this function.");
            }
            IParameterObject someParameters = (IParameterObject) parameterValue;
            if(parameterCanBeDisplayed(someParameters) ){
                stack.push(guiFormat(someParameters));
                return;
            }
            throw new ParseException("Parameter " + someParameters.toString() + " cannot be displayed. Did you want a different parameter reporting function?");
        }
    }

    private class P1ParameterTableFunction extends PostfixMathCommand {
        public P1ParameterTableFunction() {
            numberOfParameters = 6;
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack);
            Number row = (Number) stack.pop();
            Number column = (Number) stack.pop();
            String modelPath = (String) stack.pop();
            String versionNumber = (String) stack.pop();
            String paramName = (String) stack.pop();
            String modelName = (String) stack.pop();

            Object parameterValue = getModelParameterObject(modelPath, versionNumber, paramName, modelName);
            if( (! (parameterValue instanceof AbstractMultiDimensionalParameter))){
                throw new ParseException("Parameter at path " + modelPath +" is of type: " + parameterValue.getClass() + ". Not compatible with this function.");
            }
            Object aValue = ((AbstractMultiDimensionalParameter) parameterValue).getValueAt(row.intValue(), column.intValue());
            if(parameterCanBeDisplayed(aValue) ){
                stack.push(guiFormat(aValue));
                return;
            }
            throw new ParseException("Parameter " + aValue.toString() + " cannot be displayed. Did you want a different parameter reporting function?");
        }
    }

    private String guiFormat(Object object) throws ParseException {
        if(     object instanceof Integer ||
                object instanceof Double ||
                object instanceof String ||
                object instanceof Enum
        ) {
            return object.toString();
        }
        if(object instanceof DateTime) {
            return formatDate.print((DateTime) object);
        }
        if(object instanceof IParameterObject) {
            return ((IParameterObject) object).getType().toString() ;
        }
        throw new ParseException("Unknown parameter type : " + object.toString());
    }

    private boolean parameterCanBeDisplayed(Object parameterValue) {
        return
                parameterValue instanceof Integer ||
                        parameterValue instanceof Double ||
                        parameterValue instanceof String ||
                        parameterValue instanceof Enum ||
                        parameterValue instanceof DateTime ||
                        parameterValue instanceof IParameterObject
                ;
    }


}