package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.search.DocumentFactory
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.workflow.Status


public interface IColumnDescriptor {

    List<String> getValues()

    String getSearchPropertyName()

    public static class TagColumnDescriptor implements IColumnDescriptor {

        @Override
        String getSearchPropertyName() {
            return DocumentFactory.TAGS_FIELD
        }

        @Override
        List<String> getValues() {
            return Tag.list()*.name
        }
    }

    public static class StateColumnDescriptor implements IColumnDescriptor {

        @Override
        String getSearchPropertyName() {
            return DocumentFactory.STATE_FIELD
        }

        @Override
        List<String> getValues() {
            return Status.values()*.toString()
        }
    }
}