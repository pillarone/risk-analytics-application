package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import com.ulcjava.base.application.table.TableRowFilter
import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.table.TableRowFilter.AbstractFilter
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.WildCardPath

/**
 * @author martin.melchior
 */
class FilterFactory {

    static String NONE = "no filter"
    static String TEMPLATES = "templates"
    static String REGEX = "string search"
    static String EMPTY = "empty fields"
    static String[] FILTERNAMES = [NONE, TEMPLATES, REGEX, EMPTY]

    OutputElementTableModel tableModel

    FilterFactory(OutputElementTableModel tableModel) {
        this.tableModel = tableModel
    }

    TableRowFilter getFilter(String type, Object value, String category) {
        if (type.equals(REGEX) && value && value instanceof String) {
            return getRegexFilter((String)value, category)
        } else if (type.equals(TEMPLATES)) {
            return getTemplatePathFilter()
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

    TableRowFilter getTemplatePathFilter() {
        int[] indices = new int[1]
        indices[0] = tableModel.getColumnIndex(OutputElement.PATH)
        return new TemplatePathFilter(indices)
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


    private class TemplatePathFilter extends AbstractFilter {

        Map<OutputElement, WildCardPath> cache = null
        
        TemplatePathFilter(int[] columns) {
            super(columns);
        }

        protected boolean include(TableRowFilter.RowEntry rowEntry, int index) {
            if (index != tableModel.getColumnIndex(OutputElement.PATH)) false

            OutputElementTableModel model = (OutputElementTableModel) rowEntry.getModel()
            if (cache==null) {
                cache = [:]
                for (WildCardPath wcp : model.getCategoryMapping().wildCardPaths.values()) {
                    OutputElement el =  model.getAllElements().find { it -> it.templatePath == wcp.templatePath}
                    cache[el] = wcp
                }
            }
            model.isTemplateMode=false
            int pathColIndex = model.getColumnIndex(OutputElement.PATH)
            int fieldColIndex = model.getColumnIndex(OutputElement.FIELD)
            OutputElement el = cache.keySet().find {it -> it.path==rowEntry.getStringValue(pathColIndex) && it.field==rowEntry.getStringValue(fieldColIndex)}
            model.isTemplateMode=true
            return el != null
        }
    }
}
