import org.apache.ivy.plugins.resolver.URLResolver
//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-master"

grails.compiler.dependencies = {
    fileset(dir: "${grailsSettings.projectPluginsDir}", includes: "*/web-app/lib/*.jar")
    fileset(dir: "${basedir}/web-app", includes: "lib/*.jar")
}

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()
    }

    def myResolver = new URLResolver()
    myResolver.addArtifactPattern "https://svn.intuitive-collaboration.com/GrailsPlugins/grails-[artifact]/tags/LATEST_RELEASE/grails-[artifact]-[revision].[ext]"

    resolver myResolver
}