package org.pillarone.riskanalytics.application.ui.main.view


import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.search.AbstractMultiValueFilter
import org.pillarone.riskanalytics.core.workflow.Status

public interface IColumnDescriptor {

    List<String> getValues()

    AbstractMultiValueFilter getFilter(FilterDefinition filterDefinition)

    public static class TagColumnDescriptor implements IColumnDescriptor {

        @Override
        AbstractMultiValueFilter getFilter(FilterDefinition filterDefinition) {
            return filterDefinition.tagFilter
        }

        @Override
        List<String> getValues() {
            return Tag.list()*.name
        }
    }

    public static class StateColumnDescriptor implements IColumnDescriptor {

        @Override
        AbstractMultiValueFilter getFilter(FilterDefinition filterDefinition) {
            return filterDefinition.statusFilter
        }

        @Override
        List<String> getValues() {
            return Status.values()*.toString()
        }
    }
}