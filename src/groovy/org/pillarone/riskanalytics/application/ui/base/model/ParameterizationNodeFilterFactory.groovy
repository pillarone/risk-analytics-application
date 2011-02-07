package org.pillarone.riskanalytics.application.ui.base.model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationNodeFilterFactory {

    /**
     * create a parameterization node filter by a list of found items
     * @param luceneString formatted by type parameterization  as follow: parameterizationName /// tag0, tag1,...
     * @return list of ParameterizationNodeFilter
     */
    static ParameterizationNodeFilter getParameterizationNodeFilter(List<String> luceneStrings) {
        if (!luceneStrings || luceneStrings.size() == 0) return new ParameterizationNodeFilter([], ModellingInformationTableTreeModel.NAME)
        List<String> modellingItemNames = []
        for (String luceneString: luceneStrings) {
            modellingItemNames << getModellingItemName(luceneString)
        }
        return new ParameterizationNodeFilter(modellingItemNames, ModellingInformationTableTreeModel.NAME)
    }

    static String getModellingItemName(String luceneString) {
        println "initial string ${luceneString}"
        String modellingItem = null
        int index = luceneString.indexOf("TAG_SEPARTOR")
        if (luceneString && index != -1) {
            //parameterization contains at least one tag
            modellingItem = luceneString.substring(0, index).trim()
        } else {
            modellingItem = luceneString
        }
        println "${modellingItem}"
        return modellingItem
    }
}
