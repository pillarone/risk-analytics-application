package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.main.model.ModellingItemSearchBean

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationNodeFilterFactory {

    /**
     * create a parameterization node filter by a list of found items
     * @param luceneString formatted by type parameterization  as follow: parameterizationName /// tag0, tag1,...
     * @return ModellingItemNodeFilter
     */
    static ModellingItemNodeFilter getModellingNodeFilter(List<String> luceneStrings) {
        if (!luceneStrings || luceneStrings.size() == 0) return new ModellingItemNodeFilter([], ModellingInformationTableTreeModel.NAME)
        List<String> modellingItemNames = []
        for (String luceneString: luceneStrings) {
            modellingItemNames << getModellingItemName(luceneString)
        }
        return new ModellingItemNodeFilter(modellingItemNames, ModellingInformationTableTreeModel.NAME)
    }

    static String getModellingItemName(String luceneString) {
        String modellingItem = null
        int index = luceneString.indexOf(ModellingItemSearchBean.SEPARATOR)
        if (luceneString && index != -1) {
            //parameterization contains at least one tag
            modellingItem = luceneString.substring(0, index).trim()
        } else {
            modellingItem = luceneString
        }
        return modellingItem
    }
}
