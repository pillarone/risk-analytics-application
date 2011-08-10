package org.pillarone.riskanalytics.application.reports.gira.model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MultiPathFilter implements IPathFilter {

    List<IPathFilter> filters

    public MultiPathFilter(List<IPathFilter> filters) {
        this.filters = filters
    }

    public boolean accept(String path) {
        for (IPathFilter filter: filters) {
            if (!filter.accept(path))
                return false
        }
        return true
    }


}
