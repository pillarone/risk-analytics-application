package org.pillarone.riskanalytics.application.ui.customtable.model

import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

static class CustomTableHelper {
    public static Pattern variable_pattern = ~/[A-Z]+[0-9]+/
    public static Pattern range_pattern = ~/[A-Z]*[0-9]*:[A-Z]*[0-9]*/
    public static Pattern col_pattern = ~/[A-Z]+/
    public static Pattern row_pattern = ~/[0-9]+/

    public static String replaceVariables (CustomTableModel model, String formula, int cellRow, int cellCol) {
        formula = formula.replace ('$', '')

        // Check for Ranges, and replace the Range with the corresponding values
        for (String s : range_pattern.matcher(formula)) {

            String first = s.split (":")[0]
            String last = s.split (":")[1]

            String first_col_string = (col_pattern.matcher(first).find()) ? col_pattern.matcher(first)[0] : "A"
            String first_row_string = (row_pattern.matcher(first).find()) ? row_pattern.matcher(first)[0] : 1
            String last_col_string = (col_pattern.matcher(last).find()) ? col_pattern.matcher(last)[0] : CustomTableHelper.getColString(model.columnCount)
            String last_row_string = (row_pattern.matcher(last).find()) ? row_pattern.matcher(last)[0] : model.rowCount

            int first_col = CustomTableHelper.getColNo(first_col_string)-1
            int first_row = Integer.parseInt(first_row_string)-1
            int last_col = CustomTableHelper.getColNo(last_col_string)-1
            int last_row = Integer.parseInt(last_row_string)-1

            if (first_col >= model.columnCount || last_col >= model.columnCount ||
                first_row >= model.rowCount    || last_row >= model.rowCount) {
                return "#Cell doesn't exists"
            }

            StringBuilder range = new StringBuilder()
            for (int col = first_col; col <= last_col; col++) {
                for (int row = first_row; row <= last_row; row++) {
                    if (row == cellRow && col == cellCol) {
                        System.out.println ("Zirkelbezug")
                        continue
                    }

                    // TODO: if value ist to big, it is converted to a number with format (e.g. 1000E7)
                    Object value = model.getValueAt(row, col)
                    if (value instanceof Number) {
                        range.append (value)
                        range.append (",")
                    }
                }
            }
            if (range.toString().endsWith(","))
                range.deleteCharAt(range.length()-1)

            formula = formula.replace (s, range.toString().replace('E', '#')) // TODO: hack
        }

        // Check for other variables and replace them with their value
        for (String variable : variable_pattern.matcher(formula)) {
            int col = getCol (variable)
            int row = getRow (variable)

            if (col >= model.columnCount ||
                row >= model.rowCount) {
                return "#Cell doesn't exists"
            }

            if (row == cellRow && col == cellCol) {
                System.out.println ("Zirkelbezug")
                formula = formula.replace (variable + ",", "")
                formula = formula.replace ("," + variable, "")
                continue
            }
            formula = formula.replace (variable, model.getValueAt(row, col).toString())
        }

        return formula.replace ('#', 'E') // TODO: hack
    }

    public static List<String> getVariables (CustomTableModel model, String formula, int cellRow, int cellCol) {
        formula = formula.replace ('$', '')

        List<String> variables = new LinkedList<String>()

        // Check for Ranges, and replace the Range with the corresponding values
        for (String s : range_pattern.matcher(formula)) {

            String first = s.split (":")[0]
            String last = s.split (":")[1]

            String first_col_string = (col_pattern.matcher(first).find()) ? col_pattern.matcher(first)[0] : "A"
            String first_row_string = (row_pattern.matcher(first).find()) ? row_pattern.matcher(first)[0] : 1
            String last_col_string = (col_pattern.matcher(last).find()) ? col_pattern.matcher(last)[0] : CustomTableHelper.getColString(model.columnCount)
            String last_row_string = (row_pattern.matcher(last).find()) ? row_pattern.matcher(last)[0] : model.rowCount

            int first_col = CustomTableHelper.getColNo(first_col_string)-1
            int first_row = Integer.parseInt(first_row_string)-1
            int last_col = CustomTableHelper.getColNo(last_col_string)-1
            int last_row = Integer.parseInt(last_row_string)-1

            for (int col = first_col; col <= last_col; col++) {
                for (int row = first_row; row <= last_row; row++) {
                    if (row == cellRow && col == cellCol) {
                        continue
                    }

                    if (variables.contains(getVariable (row, col)) == false)
                        variables.add (getVariable (row, col))
                }
            }
            formula = formula.replace (s, "")
        }

        // Check for other variables and replace them with their value
        for (String variable : variable_pattern.matcher(formula)) {
            if (variables.contains(variable) == false)
                variables.add (variable)

            formula = formula.replace (variable, "")
        }

        return variables
    }



// Not used anymore, because JEP is evaluating mathematical expressions now
//
//    static enum Functions {
//        SUM,
//        MEAN
//    }
//
//    public static String executeFunctions (String formula) {
//
//        // T O D O: More than one function as a parameter doesn't work yet
//
//        Pattern formula_pattern = ~/[A-Z]+\([\p{Print}]*\)/
//        for (String function : formula_pattern.matcher(formula)) {
//            int bracePos      = function.indexOf("(")
//            String func       = function.substring(0, bracePos)
//            String parameters = function.substring(bracePos)
//
//            parameters = "(" + executeFunctions(parameters.substring(1, parameters.length()-1)) + ")"
//
//            switch (func) {
//                case Functions.SUM.toString():
//                    parameters = parameters.replace(',', '+')
//                    break;
//
//                case Functions.MEAN.toString():
//                    int numParam = parameters.count(",") + 1
//                    parameters = parameters.replace(',', '+')
//                    parameters += "/" + numParam
//                    break;
//            }
//
//            formula = formula.replace (function, parameters)
//        }
//
//        return formula
//    }


    public static Pattern variable_dollar_pattern = ~/[$]?[A-Z]+[$]?[0-9]+/
    public static Pattern row_range_dollar_pattern = ~/[$]?[0-9]+:[$]?[0-9]+/
    public static Pattern col_range_dollar_pattern = ~/[$]?[A-Z]+:[$]?[A-Z]+/
    public static Pattern col_dollar_pattern = ~/[$]?[A-Z]+/
    public static Pattern row_dollar_pattern = ~/[$]?[0-9]+/

    /**
     * Changes the variables in the data to copy (DataCellElement or String)
     *
     * @param data     the data to copy
     * @param row_diff the difference between the origin row and the row which the data is pasted in
     * @param col_diff the difference between the origin col and the col which the data is pasted in
     * @return the resulting data
     */
    public static Object copyData (Object data, int row_diff, int col_diff) {
        if (data instanceof DataCellElement) {
            // clone the dataCellElement
            DataCellElement dataCellElement = new DataCellElement (data)

            for (String category : dataCellElement.categoryMap.keySet()) {
                String value = dataCellElement.categoryMap[category]

                if (value.startsWith("=")) {
                    value = value.substring(1)
                    String col_string = col_dollar_pattern.matcher(value)[0]
                    String row_string = row_dollar_pattern.matcher(value)[0]

                    if (col_diff != 0 && col_string.startsWith('$') == false) {
                        int col = (col_string != null) ? CustomTableHelper.getColNo(col_string.replace('$', ''))-1 : 0
                        col_string = col_string.replace (CustomTableHelper.getColString (col+1), CustomTableHelper.getColString (col+col_diff+1))
                    }

                    if (row_diff != 0 && row_string.startsWith('$') == false) {
                        int row = (row_string != null) ? Integer.parseInt(row_string.replace('$', ''))-1 : 0
                        row_string = row_string.replace ((row+1).toString(), (row+row_diff+1).toString())
                    }
                    dataCellElement.categoryMap[category] = "=" + col_string + row_string
                }
            }
            return dataCellElement
        }

        if (data instanceof String) {
            if (data.startsWith("=")) {

                 // Check for variables
                for (String variable : variable_dollar_pattern.matcher(data)) {
                    String col_string = col_dollar_pattern.matcher(variable)[0]
                    String row_string = row_dollar_pattern.matcher(variable)[0]

                    if (col_diff != 0 && col_string.startsWith('$') == false) {
                        int col = (col_string != null) ? CustomTableHelper.getColNo(col_string.replace('$', ''))-1 : 0
                        col_string = col_string.replace (CustomTableHelper.getColString (col+1), CustomTableHelper.getColString (col+col_diff+1))
                    }

                    if (row_diff != 0 && row_string.startsWith('$') == false) {
                        int row = (row_string != null) ? Integer.parseInt(row_string.replace('$', ''))-1 : 0
                        row_string = row_string.replace ((row+1).toString(), (row+row_diff+1).toString())
                    }

                    // TODO: replaces all occurrences of the variable, so if e.g. B1 was changed to B2, and the next variable is also B2, which should be changed to B3, then the first variable is also changed to B3
                    data = data.replace (variable, col_string + row_string)
                }

                // Check for Row range variables
                for (String variable : row_range_dollar_pattern.matcher(data)) {
                    String first_row_string = variable.split (":")[0]
                    String second_row_string = variable.split (":")[0]
                    if (row_diff != 0) {
                        if (first_row_string.startsWith('$') == false) {
                            int row = (first_row_string != null) ? Integer.parseInt(first_row_string.replace('$', ''))-1 : 0
                            first_row_string = first_row_string.replace ((row+1).toString(), (row+row_diff+1).toString())
                        }
                        if (second_row_string.startsWith('$') == false) {
                            int row = (second_row_string != null) ? Integer.parseInt(second_row_string.replace('$', ''))-1 : 0
                            second_row_string = second_row_string.replace ((row+1).toString(), (row+row_diff+1).toString())
                        }
                        data = data.replace (variable, first_row_string + ":" + second_row_string)
                    }
                }

                // Check for Column range variables
                for (String variable : col_range_dollar_pattern.matcher(data)) {
                    String first_col_string = variable.split (":")[0]
                    String second_col_string = variable.split (":")[0]
                    if (col_diff != 0) {
                        if (first_col_string.startsWith('$') == false) {
                            int col = (first_col_string != null) ? CustomTableHelper.getColNo(first_col_string.replace('$', ''))-1 : 0
                            first_col_string = first_col_string.replace (CustomTableHelper.getColString (col+1), CustomTableHelper.getColString (col+col_diff+1))
                        }
                        if (second_col_string.startsWith('$') == false) {
                            int col = (second_col_string != null) ? CustomTableHelper.getColNo(second_col_string.replace('$', ''))-1 : 0
                            second_col_string = second_col_string.replace (CustomTableHelper.getColString (col+1), CustomTableHelper.getColString (col+col_diff+1))
                        }
                        data = data.replace (variable, first_col_string + ":" + second_col_string)
                    }
                }

                return data
            }
        }

        return data
    }

    /**
     * Converts a Excel-Like String into a int-Value where A=1, B=2, AA=27, AB=28, ...
     *
     * @param colString The Excel-Like input-String
     * @return          The number which represents the input-String
     */
    public static int getColNo (String colString) {
        int col = 0
        for (int i = 0; i < colString.length(); i++) {
            col += (colString.charAt(i).toUpperCase() - 64) * (Math.pow (26, (colString.length()-1-i)))
        }
        return col
    }

    /**
     * Returns a Excel-Like String in the form of "ABA" representing the input value, where 1=A, 2=B, 27=AA, 28=AB, ...
     *
     * @param col   The number to be transformed to a String
     * @return      The number represented by a Excel-Like String
     */
    public static String getColString (int col) {
        col--
        int rest = col % 26
        int times = col / 26

        char letter = (char)(rest + 65)
        if (times == 0) {
            return letter.toString()
        }

        String value = getColString (times) + letter.toString()
        return value
    }


    public static String getVariable (int row, int col) {
        return getColString(col+1) + (row+1).toString()
    }

    public static int getCol (String variable) {
        String col_string = col_pattern.matcher(variable)[0]
        int col = (col_string != null) ? CustomTableHelper.getColNo(col_string)-1 : 0
        return col
    }

    public static int getRow (String variable) {
        String row_string = row_pattern.matcher(variable)[0]
        int row = (row_string != null) ? Integer.parseInt(row_string)-1 : 0
        return row
    }
}
