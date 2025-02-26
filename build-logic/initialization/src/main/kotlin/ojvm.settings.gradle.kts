import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("ojvm.develocity")
    id("ojvm.gradm")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("ojvm")
