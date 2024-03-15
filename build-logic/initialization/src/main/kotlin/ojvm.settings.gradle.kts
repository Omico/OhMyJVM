import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("ojvm.gradm")
    id("ojvm.gradle-enterprise")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("ojvm")
