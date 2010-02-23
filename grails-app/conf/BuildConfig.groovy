//Use a custom project plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-0.5.x"

grails.plugin.repos.discovery.pillarone = "https://readplugins:readplugins@svn.intuitive-collaboration.com/GrailsPlugins/"

grails.plugin.repos.resolveOrder = ['pillarone', 'default', 'core']

grails.compiler.dependencies = {
    fileset(dir: "${grailsSettings.projectPluginsDir}", includes: "*/web-app/lib/*.jar")
    fileset(dir: "${basedir}/web-app", includes: "lib/*.jar")
}