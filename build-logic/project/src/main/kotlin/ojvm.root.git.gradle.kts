import me.omico.consensus.api.dsl.requireRootProject

plugins {
    id("me.omico.consensus.git")
}

requireRootProject()

consensus {
    git {
        hooks {
            preCommit {
                correctGradleWrapperScriptPermissions()
                checkGitAttributes()
            }
        }
    }
}
