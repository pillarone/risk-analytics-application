//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-kti"

grails.plugin.repos.discovery.pillarone = "https://readplugins:readplugins@svn.intuitive-collaboration.com/GrailsPlugins/"

grails.plugin.repos.resolveOrder = ['pillarone', 'default', 'core']

//grails.plugin.location.'risk-analytics-core' = "../RiskAnalyticsCore"

grails.compiler.dependencies = {
    fileset(dir: "${grailsSettings.projectPluginsDir}", includes: "*/web-app/lib/*.jar")
    fileset(dir: "${basedir}/web-app", includes: "lib/*.jar")
}