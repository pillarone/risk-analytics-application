package org.pillarone.riskanalytics.application.reports.gira.model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PathFilter implements IPathFilter {

    String start

    public PathFilter(String start) {
        this.start = start
    }

    public boolean accept(String path) {
        return path.startsWith(start)
    }

    static IPathFilter getFilter(String pathStart, List<String> suffixes) {
        return new MultiPathFilter([new PathFilter(pathStart), new SuffixPathFilter(suffixes)])
    }


}
