package org.pillarone.riskanalytics.application.ui.customtable.model

import org.nfunk.jep.function.PostfixMathCommand
import org.nfunk.jep.ParseException
import org.nfunk.jep.JEP
import org.nfunk.jep.function.Sum
import org.nfunk.jep.function.Abs

/**
 *
 * @author ivo.nussbaumer
 */
public class MathParser extends JEP {

    /**
     * Constructor
     */
    public MathParser () {
        this.addFunction("sum", new Sum())
        this.addFunction("abs", new Abs())

        this.addFunction("mean", new Mean())
        this.addFunction("min", new Min())
        this.addFunction("max", new Max())
        this.addFunction("median", new Median())
        this.addFunction("stddev", new StdDev())
    }

    /**
     * Mean Function for the MathParser JEP
     */
    private class Mean extends PostfixMathCommand {
        public Mean () {
            // set numberOfParameters to -1 -> variable number of parameters
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Mean")

            double sum = 0.0
            int numValues = 0
            Object value

            // get the parameters from the stack, and calc the sum
            while (!stack.empty()) {
                value = stack.pop()
                if (value instanceof Number) {
                    sum += value
                    numValues++
                }
            }

            // calc the mean
            double mean = sum / numValues

            // push the result back on the now empty stack
            stack.push(mean);
        }
    }

    private class Min extends PostfixMathCommand {
        public Min () {
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Min")

            double minValue = Double.MAX_VALUE
            Object value

            while (!stack.empty()) {
                value = stack.pop()
                if (value instanceof Number && value < minValue) {
                    minValue = value
                }
            }
            stack.push(minValue);
        }
    }

    private class Max extends PostfixMathCommand {
        public Max () {
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Max")

            double maxValue = Double.MIN_VALUE
            Object value

            while (!stack.empty()) {
                value = stack.pop()
                if (value instanceof Number && value > maxValue) {
                    maxValue = value
                }
            }
            stack.push(maxValue);
        }
    }

    private class Median extends PostfixMathCommand {
        public Median () {
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Median")

            List params = new LinkedList<Number>()
            Object value

            while (!stack.empty()) {
                value = stack.pop()
                if (value instanceof Number) {
                    params.add(value)
                }
            }

            params.sort()
            double median
            if (params.size() % 2 == 0) {
                median = (params[(int)(params.size()/2-1)] + params[(int)(params.size()/2)]) / 2
            } else {
                median = params[(int)(params.size()/2)]
            }
            stack.push(median);
        }
    }

    private class StdDev extends PostfixMathCommand {
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
    }
}