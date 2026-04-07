import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.pipelines.*
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {

    pipeline(RecipesFinderPipeline)
}


object RecipesFinderPipeline : Pipeline({
    name = "RecipesFinder: pipeline"

    repositories {
        repository(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                +pr:*
            """.trimIndent()
        }
    }

    integrations {
        dockerRegistry {
            id = "Docker_0"
            name = "Docker"
            userName = "dariakrup"
            password = "credentialsJSON:82cbcea7-18a1-4a18-9e08-c383d88d5f4f"
        }
    }

    job(RecipesFinderPipeline_Job1)
})

object RecipesFinderPipeline_Job1 : Job({
    id("Job1")
    name = "Job 1"

    integration("Docker", "Docker_0")

    steps {
        script {
            name = "Script"
            scriptContent = "echo ./README.md"
            dockerImage = "ubuntu:latest"
        }
    }
})
