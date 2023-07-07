import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
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

version = "2023.05"

project {

    buildType(Build)

    features {
        amazonEC2CloudImage {
            id = "PROJECT_EXT_2"
            profileId = "amazon-1"
            agentPoolId = "-2"
            name = "AMI Ubuntu"
            vpcSubnetId = "subnet-0c23f411b0800b216"
            keyPairName = "daria.krupkina"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            source = Source("ami-07908fe7a17542f6b")
        }
        amazonEC2CloudProfile {
            id = "amazon-1"
            name = "Cloud AWS Profile"
            terminateIdleMinutes = 0
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:c4151395-a0a5-4db6-9697-918ebca829e5"
                secretKey = "credentialsJSON:42f04976-3912-4b71-8161-3e9ca9484e7d"
            }
            param("terminate-after-build", "false")
            param("terminateTimeOut_checkbox", "false")
        }
    }
}

object Build : BuildType({
    name = "Build"

    enablePersonalBuilds = false
    maxRunningBuilds = 1

    params {
        param("parameter_for_url", "")
    }

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    steps {
        gradle {
            tasks = "clean build"
            gradleWrapperPath = ""
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
