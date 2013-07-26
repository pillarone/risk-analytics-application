package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import org.pillarone.riskanalytics.application.search.DocumentFactory


class FilterDefinition {

    private ListMultimap<String, String> queryParts = ArrayListMultimap.create()

    void setText(String text) {
        queryParts.removeAll(DocumentFactory.NAME_FIELD)
        queryParts.put(DocumentFactory.NAME_FIELD, text)
    }

    void putQueryPart(String field, String value) {
        queryParts.put(field, value)
    }

    void clearField(String field) {
        queryParts.removeAll(field)
    }

    List<String> getActiveValues(String field) {
        return queryParts.get(field)
    }


    String toQuery() {
        StringBuilder query = new StringBuilder()
        Iterator<String> keyIterator = queryParts.keySet().iterator()
        while (keyIterator.hasNext()) {
            String key = keyIterator.next()

            Iterator<String> iterator = queryParts.get(key).iterator()
            query.append("(")
            while (iterator.hasNext()) {
                String value = iterator.next()
                query.append(key).append(":").append(value)

                if (iterator.hasNext()) {
                    query.append(" OR ")
                }
            }
            query.append(")")


            if (keyIterator.hasNext()) {
                query.append(" AND ")
            }
        }

        return query.toString()
    }

}
