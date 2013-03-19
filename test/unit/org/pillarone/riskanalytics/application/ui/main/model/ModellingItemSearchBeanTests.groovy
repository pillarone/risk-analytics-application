package org.pillarone.riskanalytics.application.ui.main.model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingItemSearchBeanTests extends GroovyTestCase {

    public void testPerformSearch() {
        ModellingItemIndexer indexer = new ModellingItemIndexer(getItemNames())
        ModellingItemSearchBean searchBean = new ModellingItemSearchBean(indexer: indexer)

        List results = searchBean.performSearch("One")
        assertEquals 3, results.size()

        results = searchBean.performSearch("2011.02.18")
        assertEquals 3, results.size()

        results = searchBean.performSearch("2011*")
        assertEquals 8, results.size()

        results = searchBean.performSearch("*2011.02.*")
        assertEquals 8, results.size()

        results = searchBean.performSearch("*02*")
        assertEquals 8, results.size()

        results = searchBean.performSearch("*02* *one*")
        assertEquals 9, results.size()

        results = searchBean.performSearch("02 one")
        assertEquals 9, results.size()

        results = searchBean.performSearch("one CapitalEagle  18")
        assertEquals 12, results.size()

        results = searchBean.performSearch("one ' / \" test")
        assertEquals 5, results.size()

        results = searchBean.performSearch('"CapitalEagle NP"')
        assertEquals 5, results.size()

        results = searchBean.performSearch('CapitalEagle NP')
        assertEquals 8, results.size()

        results = searchBean.performSearch('CapitalEagle AND NP')
        assertEquals 5, results.size()

        results = searchBean.performSearch('CapitalEagle OR NP')
        assertEquals 8, results.size()

        results = searchBean.performSearch('"One Line Example" Aggregated')
        assertEquals 6, results.size()

        results = searchBean.performSearch('"One Line Example" AND Aggregated')
        assertEquals 1, results.size()
    }

    List<String> getItemNames() {
        List<String> names = []
        names << "One Line Example  +++  2011.02.18 17:27:00"
        names << "CapitalEagle NP+ALL50"
        names << "CapitalEagle NP+MTPL50"
        names << "CapitalEagle NP  +++  2011.02.22 18:24:32"
        names << "CapitalEagle PEAK"
        names << "One Line Example2"
        names << "CapitalEagle NP+ALL50"
        names << "test"
        names << "test2"
        names << "AEP Transaction"
        names << "Astra Zeneca"
        names << "French Corporate"
        names << "MandB"
        names << "Omega  +++  2011.02.22 17:27:54"
        names << "RIp"
        names << "RMS Test Example"
        names << "UK Corporate 1"
        names << "understandPattern"
        names << "CapitalEagle Analysis"
        names << "CapitalEagle Analysis Drill Down"
        names << "Aggregated Overview  +++  2011.02.18 17:27:00"
        names << "Lines of Business, Claims, R/I"
        names << "Master"
        names << "Cashflows only"
        names << "Master ex. Accounting"
        names << "Details, aggregated"
        names << "Different Views"
        names << "Details short, aggregated"
        names << "Single Gross Ultimates  +++  2011.02.22 17:27:54"
        names << "Details, single"
        names << "2011.02.18 17:27:00  +++  One Line Example  +++  Aggregated Overview"
        names << "2011.02.22 17:27:54  +++  Omega  +++  Single Gross Ultimates"
        names << "2011.02.22 18:24:32  +++  CapitalEagle NP  +++  Aggregated Overview"
        return names
    }

}
