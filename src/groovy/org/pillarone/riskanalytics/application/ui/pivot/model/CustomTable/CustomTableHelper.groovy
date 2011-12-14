package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable


static class CustomTableHelper {
    public static String replaceVariables (CustomTableModel model, String formula, int cellRow, int cellCol) {
        StringBuilder sb = new StringBuilder(formula)

        int colStart = -1
        int rowStart = -1

        int col2Start = -1
        int row2Start = -1
        int row2End = -1

        for (int i = 0; i <= sb.length(); i++) {
            if (i < sb.length() && sb.charAt (i).isLetter()) {
                // Start Col of Variable
                if (colStart == -1) {
                    colStart = i
                }
            }
            if (i < sb.length() && sb.charAt (i).isDigit()) {
                // Start Row of Variable
                if (rowStart == -1) {
                    rowStart = i
                }
            }
            if (i < sb.length() && sb.charAt (i) == ':') {
                col2Start = colStart
                row2Start = rowStart
                row2End   = i

                colStart = -1
                rowStart = -1
                continue
            }

            // End of Variable
            if (i >= sb.length() || sb.charAt(i).isLetterOrDigit() == false) {

                // Range Complete Variable
                if (col2Start != -1 && row2Start != -1 && colStart != -1 && rowStart != -1) {
                    int col1 = getColNo (sb.substring(col2Start, row2Start))-1
                    int row1 = Integer.parseInt(sb.substring(row2Start, row2End))-1
                    int col2 = getColNo (sb.substring(colStart, rowStart))-1
                    int row2 = Integer.parseInt(sb.substring(rowStart, i))-1

                    StringBuilder insertString = new StringBuilder()
                    for (int col = col1; col <= col2; col++) {
                        for (int row = row1; row <= row2; row++) {
                            if (cellCol == col && cellRow == row) {
                                System.out.println("Zirkelbezug")
                                continue
                            }
                            Object value = model.getValueAt (row, col)
                            if (value == "") {
                                continue
                            }
                            insertString.append (value)
                            insertString.append (";")
                        }
                    }
                    insertString.deleteCharAt(insertString.length()-1)

                    sb.delete(col2Start, i)
                    sb.insert(col2Start, insertString.toString())
                    i = i - (i-col2Start) + insertString.length()

                    colStart = -1
                    rowStart = -1
                    col2Start = -1
                    row2Start = -1
                }

                // Range Col Variable
                if (col2Start != -1 && row2Start == -1 && colStart != -1 && rowStart == -1) {
                    int col1 = getColNo (sb.substring(col2Start, row2End))-1
                    int col2 = getColNo (sb.substring(colStart, i))-1

                    StringBuilder insertString = new StringBuilder()
                    for (int col = col1; col <= col2; col++) {
                        for (int row = 0; row < model.rowCount; row++) {
                            if (cellCol == col && cellRow == row) {
                                System.out.println("Zirkelbezug")
                                continue
                            }
                            Object value = model.getValueAt (row, col)
                            if (value == "") {
                                continue
                            }
                            insertString.append (value)
                            insertString.append (";")
                        }
                    }
                    insertString.deleteCharAt(insertString.length()-1)

                    sb.delete(col2Start, i)
                    sb.insert(col2Start, insertString.toString())
                    i = i - (i-col2Start) + insertString.length()

                    colStart = -1
                    col2Start = -1
                }

                // Range Row Variable
                if (col2Start == -1 && row2Start != -1 && colStart == -1 && rowStart != -1) {
                    int row1 = Integer.parseInt(sb.substring(row2Start, row2End))-1
                    int row2 = Integer.parseInt(sb.substring(rowStart, i))-1

                    StringBuilder insertString = new StringBuilder()
                    for (int col = 0; col < model.columnCount; col++) {
                        for (int row = row1; row <= row2; row++) {
                            if (cellCol == col && cellRow == row) {
                                System.out.println("Zirkelbezug")
                                continue
                            }
                            Object value = model.getValueAt (row, col)
                            if (value == "") {
                                continue
                            }
                            insertString.append (value)
                            insertString.append (";")
                        }
                    }
                    insertString.deleteCharAt(insertString.length()-1)

                    sb.delete(row2Start, i)
                    sb.insert(row2Start, insertString.toString())
                    i = i - (i-row2Start) + insertString.length()

                    rowStart = -1
                    row2Start = -1
                }

                // Complete Variable
                if (colStart != -1 && rowStart != -1 && col2Start == -1 && row2Start == -1) {
                    int col = getColNo (sb.substring(colStart, rowStart))-1
                    int row = Integer.parseInt(sb.substring(rowStart, i))-1

                    sb.delete(colStart, i)
                    String value = model.getValueAt (row, col)
                    sb.insert(colStart, value)
                    i = i - (i-colStart) + value.length()

                    if (value == "") {
                        if (sb.charAt(i) == ';') {
                            sb.deleteCharAt(i)
                        } else if (sb.charAt (colStart-1) == ';') {
                            sb.deleteCharAt(colStart-1)
                        }
                        continue
                    }


                    colStart = -1
                    rowStart = -1
                }

                // Col Variable -> Function, or Col Variable without range
                if (colStart != -1 && rowStart == -1 && col2Start == -1 && row2Start == -1) {
                    colStart = -1
                }
                // Row Variable -> Row Variable without range
                if (colStart == -1 && rowStart != -1 && col2Start == -1 && row2Start == -1) {
                    rowStart = -1
                }
            }
        }

        return sb.toString()
    }

    static enum Functions {
        SUM,
        MEAN
    }

    public static String executeFunctions (String formula) {
        StringBuilder sb = new StringBuilder(formula)

        int funStart = -1

        for (int i = 0; i < sb.length(); i++) {
            if (funStart == -1 && sb.charAt(i).isLetter()) {
                funStart = i
            }
            if (funStart != -1 && sb.charAt(i) == '(') {
                int braceEnd = sb.indexOf(')', i)
                String parameters = sb.substring(i, braceEnd+1)

                String func = sb.substring(funStart, i)
                switch (func) {
                    case Functions.SUM.toString():
                        parameters = parameters.replace(';', '+')
                        break;

                    case Functions.MEAN.toString():
                        int numParam = parameters.count(";") + 1
                        parameters = parameters.replace(';', '+')
                        parameters += "/" + numParam
                        break;
                }

                sb.delete(funStart, braceEnd+1)
                sb.insert(funStart, parameters)
                i = funStart + parameters.length()-1

                funStart = -1
            }
        }
        return sb.toString()
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
}
