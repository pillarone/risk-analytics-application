import org.apache.ivy.plugins.resolver.URLResolver
//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-v1.2"

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
    myResolver.addArtifactPattern "https://build.intuitive-collaboration.com/plugins/[artifact]/grails-[artifact]-[revision].[ext]"

    resolver myResolver
}