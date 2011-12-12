package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import com.ulcjava.base.application.table.TableRowFilter
import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.table.TableRowFilter.AbstractFilter
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel

/**
 * @author martin.melchior
 */
class FilterFactory {

    static String NONE = "none"
    static String REGEX = "regex"
    static String EMPTY = "empty"
    static String[] FILTERNAMES = [NONE, REGEX, EMPTY]

    OutputElementTableModel tableModel

    FilterFactory(OutputElementTableModel tableModel) {
        this.tableModel = tableModel
    }

    TableRowFilter getFilter(String type, Object value, String category) {
        if (type.equals(REGEX) && value && value instanceof String) {
            return getRegexFilter((String)value, category)
        } else if (type.equals(EMPTY)) {
            return getEmptyFilter(category)
        } else if (type.equals(NONE)) {
            return null
        }
        return null
    }

    TableRowFilter getRegexFilter(String regex, String category) {
        int colIndex = tableModel.getColumnIndex(category)
        if (colIndex>=0) {
            int[] colIndices = new int[1]
            colIndices[0] = colIndex
            return new TableRowFilter.RegexFilter(Pattern.compile(regex), colIndices)
        }
        return null
    }

    TableRowFilter getEmptyFilter(String category) {
        int colIndex = category != OutputElement.PATH ? tableModel.getColumnIndex(category) : -1
        if (colIndex>=0) {
            int[] colIndices = new int[1]
            colIndices[0] = colIndex
            return new EmptyFilter(colIndices)
        }
        return null
    }

    private class EmptyFilter extends AbstractFilter {

        EmptyFilter(int[] columns) {
            super(columns);
        }

        protected boolean include(TableRowFilter.RowEntry rowEntry, int index) {
            //returns the model value at the specified index for this row
            Object value = rowEntry.getValue(index);
            if (value == null || String.valueOf(value)=="") {
                return true;
            }
            return false;
        }
    }
}
