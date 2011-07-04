package org.pillarone.riskanalytics.application.reports.gira.action

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultPathParser {

    String modelName
    List<String> paths
    Map<PathType, List> cachedMap = [:]


    public ResultPathParser(String modelName,List paths) {
        this.modelName = modelName
        this.paths = paths
    }

    String getComponentPath(PathType pathType) {
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: return modelName+":claimsGenerators"
            case PathType.LINESOFBUSINESS: return modelName+":linesOfBusiness"
            case PathType.REINSURANCE: return modelName+":reinsurance"
        }
        return null
    }

    List<String> getComponentPaths(PathType pathType) {
        if (!cachedMap[pathType])
            cachedMap[pathType] = paths.findAll {String path -> path.startsWith(getComponentPath(pathType))}
        return cachedMap[pathType]
    }

    String getComponentRootPath(PathType pathType) {
        List<String> componentpaths = getComponentPaths(pathType)
        return componentpaths.find {String path -> !path.startsWith(getComponentPath(pathType) + ":sub")}
    }

}

enum PathType {
    CLAIMSGENERATORS, LINESOFBUSINESS, REINSURANCE
}