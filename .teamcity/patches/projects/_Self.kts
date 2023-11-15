package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.projectFeatures.HashiCorpVaultConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.hashiCorpVaultConnection
import jetbrains.buildServer.configs.kotlin.remoteParameters.hashiCorpVaultParameter
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the root project
accordingly, and delete the patch script.
*/
changeProject(DslContext.projectId) {
    params {
        expect {
            hashiCorpVaultParameter {
                name = "env.AWS_SECRET_ACCESS_KEY"
                readOnly = true
                query = "aws/data/access!/AWS_SECRET_ACCESS_KEY"
                namespace = "approle"
            }
        }
        update {
            hashiCorpVaultParameter {
                name = "env.AWS_SECRET_ACCESS_KEY"
                readOnly = true
                query = "aws/data/access!/AWS_SECRET_ACCESS_KEY"
                namespace = "ldap"
            }
        }
    }

    features {
        val feature1 = find<HashiCorpVaultConnection> {
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
        feature1.apply {
            param("teamcity.vault.requirement", "")
        }
    }
}
