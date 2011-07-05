package org.pillarone.riskanalytics.application.reports.gira.action

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultPathParser {

    String modelName
    List<String> paths
    Map<PathType, List> cachedMap = [:]


    public ResultPathParser(String modelName, List paths) {
        this.modelName = modelName
        this.paths = paths
    }

    String getComponentPath(PathType pathType) {
        switch (pathType) {
            case PathType.CLAIMSGENERATORS: return modelName + ":claimsGenerators"
            case PathType.LINESOFBUSINESS: return modelName + ":linesOfBusiness"
            case PathType.REINSURANCE: return modelName + ":reinsurance"
        }
        return null
    }

    List<String> getComponentPaths(PathType pathType) {
        if (!cachedMap[pathType])
            cachedMap[pathType] = paths.findAll {String path -> path.startsWith(getComponentPath(pathType))}.sort()
        return cachedMap[pathType]
    }

    String getComponentRootPath(PathType pathType) {
        List<String> componentpaths = getComponentPaths(pathType)
        return componentpaths.find {String path -> !path.startsWith(getComponentPath(pathType) + ":sub")}
    }

    PathType getPathType(String path) {
        if (path.startsWith(modelName + ":claimsGenerators")) return PathType.CLAIMSGENERATORS
        if (path.startsWith(modelName + ":linesOfBusiness")) return PathType.LINESOFBUSINESS
        if (path.startsWith(modelName + ":reinsurance")) return PathType.REINSURANCE
        return null
    }

}

enum PathType {
    CLAIMSGENERATORS, LINESOFBUSINESS, REINSURANCE

    String getDispalyName() {
        switch (this) {
            case CLAIMSGENERATORS: return "Claims Generators"
            case LINESOFBUSINESS: return "Line of Business"
            case REINSURANCE: return "Reinsurance"
        }
    }
}