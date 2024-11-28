import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.ideaDuplicates
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

version = "2024.07"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        param("env.JDK_11", "/usr/lib/jvm/java-11-openjdk-amd64")
        param("env.JDK_1_8", "/usr/lib/jvm/java-8-openjdk-amd64")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            id = "gradle_runner"
            enabled = false
            tasks = "clean build"
            gradleWrapperPath = ""
        }
        ideaDuplicates {
            id = "Duplicator"
            pathToProject = ""
            jdk {
                name = "1.8"
                path = "%env.JDK_1_8%"
                patterns("jre/lib/*.jar", "jre/lib/ext/jfxrt.jar")
                extAnnotationPatterns("%teamcity.tool.idea%/lib/jdkAnnotations.jar")
            }
            jvmArgs = "-Xmx1G -XX:ReservedCodeCacheSize=512m"
            ideaAppHome = "%teamcity.tool.idea%"
            lowerBound = 10
            discardCost = 0
            distinguishMethods = true
            distinguishTypes = true
            distinguishLiterals = true
            extractSubexpressions = true
            includeTestSources = true
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
