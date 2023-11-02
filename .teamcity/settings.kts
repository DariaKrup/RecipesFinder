import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.hashiCorpVaultConnection
import jetbrains.buildServer.configs.kotlin.remoteParameters.hashiCorpVaultParameter
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
        hashiCorpVaultConnection {
            id = "PROJECT_EXT_14"
            name = "HashiCorp Vault AWS IAM"
            namespace = "aws"
            url = "https://vault.burnasheva.click:8200/"
            authMethod = iam()
        }
        hashiCorpVaultConnection {
            id = "PROJECT_EXT_8"
            name = "HashiCorp Vault Local Approle"
            namespace = "approle"
            url = "https://vault.burnasheva.click:8200"
            authMethod = appRole {
                roleId = "e0d9ef3e-a837-c70c-ea96-46e9870e6567"
                secretId = "credentialsJSON:7b85af73-566a-40d4-a6e6-1b7cb4ab7154"
            }
        }
    }
}

object Build : BuildType({
    name = "Build"

    params {
        param("docker_password", "%vault:passwords_storage_v1/docker!/password%")
        hashiCorpVaultParameter {
            name = "env.AWS_SECRET_ACCESS_KEY"
            readOnly = true
            query = "aws/data/access!/AWS_SECRET_ACCESS_KEY"
            namespace = "approle"
        }
        hashiCorpVaultParameter {
            name = "github_token"
            display = ParameterDisplay.PROMPT
            readOnly = true
            query = "passwords_storage_v1/github!/token"
            namespace = "aws"
        }
        hashiCorpVaultParameter {
            name = "env.AWS_ACCESS_KEY_ID"
            readOnly = true
            query = "aws/data/access!/AWS_ACCESS_KEY_ID"
            namespace = "approle"
        }
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Vault Parameters"
            id = "Vault_Parameters"
            scriptContent = "echo %docker_password% %github_token% >> creds.txt"
        }
        gradle {
            tasks = "clean build"
            gradleWrapperPath = ""
        }
        script {
            scriptContent = "call gradle/.bat"
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
