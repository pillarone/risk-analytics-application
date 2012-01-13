package org.pillarone.riskanalytics.application.ui.customtable.model

import org.nfunk.jep.function.PostfixMathCommand
import org.nfunk.jep.function.Add
import org.nfunk.jep.ParseException

/**
 *
 * @author ivo.nussbaumer
 */
private class Mean extends PostfixMathCommand {
        public Mean () {
            numberOfParameters = -1
        }
        public void run(Stack stack) throws ParseException {
            checkStack(stack) // check the stack

            if (curNumberOfParameters < 1) throw new ParseException("No arguments for Mean")

            double sum = 0.0
            int numValues = 0
            Object value

            while (stack.empty() == false) {
                value = stack.pop()
                if (value instanceof Number) {
                    sum += value
                    numValues++
                }
            }
            double mean = sum / numValues

            stack.push(mean);
        }
    }
