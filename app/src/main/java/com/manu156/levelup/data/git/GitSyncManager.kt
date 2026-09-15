package com.manu156.levelup.data.git

import android.content.Context
import android.util.Log
import com.manu156.levelup.data.model.WorkSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.json.JSONObject
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class GitSyncResult {
    data class Success(val message: String, val sessionsPushed: Int = 0, val sessionsPulled: Int = 0) : GitSyncResult()
    data object AlreadyUpToDate : GitSyncResult()
    data class Conflicts(val conflictItems: List<GitConflictItem>) : GitSyncResult()
    data class Failure(val errorMessage: String, val throwable: Throwable? = null) : GitSyncResult()
}

data class GitConflictItem(
    val relativePath: String,
    val sessionId: String?,
    val sessionTimeFormatted: String?,
    val localSummary: String,
    val remoteSummary: String,
    val localRawJson: String,
    val remoteRawJson: String
)

enum class ConflictResolutionChoice {
    KEEP_LOCAL,
    KEEP_REMOTE
}

class GitSyncManager(private val context: Context) {

    private val localRepoDir = File(context.filesDir, "levelup_git_repo")

    fun getSavedConfig(): GitSyncConfig = GitSyncConfig.load(context)

    /**
     * Tests GitHub connectivity and credentials without modifying local storage.
     */
    suspend fun testConnection(config: GitSyncConfig): Result<String> = withContext(Dispatchers.IO) {
        if (!config.isConfigured) {
            return@withContext Result.failure(IllegalArgumentException("Repository URL and Personal Access Token are required."))
        }
        try {
            val credentials = UsernamePasswordCredentialsProvider(config.personalAccessToken, "")
            val refs = Git.lsRemoteRepository()
                .setRemote(config.sanitizedRemoteUrl)
                .setCredentialsProvider(credentials)
                .call()
            Result.success("Connection successful! Found ${refs.size} remote references.")
        } catch (e: Exception) {
            Log.e(TAG, "GitHub connection test failed", e)
            Result.failure(Exception("Connection failed: ${e.message ?: "Invalid token or repository URL"}", e))
        }
    }

    /**
     * Primary Flow: Pushes local sessions to the remote GitHub repository.
     * Dumps local data into partitioned JSON files, commits, merges remote updates,
     * and pushes to origin/branch. If merge conflicts occur, returns GitSyncResult.Conflicts.
     */
    suspend fun pushToRemote(
        sessions: List<WorkSession>,
        config: GitSyncConfig
    ): GitSyncResult = withContext(Dispatchers.IO) {
        if (!config.isConfigured) {
            return@withContext GitSyncResult.Failure("GitHub Sync is not configured. Please set repository URL and token in Settings.")
        }

        try {
            val targetBranch = config.branch.trim().ifBlank { "main" }
            val git = openOrCreateRepository(config)
            val credentials = UsernamePasswordCredentialsProvider(config.personalAccessToken, "")

            for (session in sessions) {
                val relPath = GitSessionSerializer.getRelativePathForSession(session)
                val targetFile = File(localRepoDir, relPath)
                targetFile.parentFile?.mkdirs()
                targetFile.writeText(GitSessionSerializer.serializeSession(session))
            }

            ensureRepoMetadata()

            git.add().addFilepattern(".").call()

            val status = git.status().call()
            val hasLocalChanges = status.hasUncommittedChanges() || status.untracked.isNotEmpty()
            val headCommit = git.repository.resolve(Constants.HEAD)

            if (headCommit == null || hasLocalChanges) {
                git.commit()
                    .setMessage(if (headCommit == null) "Initial sync: ${sessions.size} sessions • ${Instant.now()}" else "LevelUp sync: ${sessions.size} sessions • ${Instant.now()}")
                    .setAuthor(config.authorName, config.authorEmail)
                    .call()
            }

            try {
                git.fetch()
                    .setRemote("origin")
                    .setCredentialsProvider(credentials)
                    .call()
            } catch (e: Exception) {
                Log.w(TAG, "Initial fetch warning: ${e.message}")
            }

            val remoteBranchRef = git.repository.findRef("refs/remotes/origin/$targetBranch")
            if (remoteBranchRef != null) {
                val pullResult = git.pull()
                    .setRemote("origin")
                    .setRemoteBranchName(targetBranch)
                    .setCredentialsProvider(credentials)
                    .call()

                if (!pullResult.isSuccessful) {
                    val mergeResult = pullResult.mergeResult
                    if (mergeResult != null && mergeResult.mergeStatus == MergeResult.MergeStatus.CONFLICTING) {
                        Log.w(TAG, "Conflicts encountered during pull: ${mergeResult.conflicts.keys}")
                        val conflicts = extractConflicts(git, mergeResult.conflicts.keys)
                        return@withContext GitSyncResult.Conflicts(conflicts)
                    }
                }
            }

            val pushResults = git.push()
                .setRemote("origin")
                .setRefSpecs(RefSpec("refs/heads/$targetBranch:refs/heads/$targetBranch"))
                .setCredentialsProvider(credentials)
                .call()

            val latestCommit = git.repository.resolve(Constants.HEAD)?.name
            GitSyncConfig.save(
                context,
                config.copy(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    lastSyncCommitHash = latestCommit
                )
            )

            GitSyncResult.Success(
                message = "Successfully pushed ${sessions.size} sessions to GitHub!",
                sessionsPushed = sessions.size
            )
        } catch (e: Exception) {
            Log.e(TAG, "Push to GitHub failed", e)
            GitSyncResult.Failure("Push failed: ${e.message ?: e.javaClass.simpleName}", e)
        }
    }

    /**
     * Secondary Flow: Manually pulls sessions from GitHub and returns them
     * to be upserted into local storage.
     */
    suspend fun pullFromRemote(
        config: GitSyncConfig
    ): Pair<GitSyncResult, List<WorkSession>?> = withContext(Dispatchers.IO) {
        if (!config.isConfigured) {
            return@withContext Pair(
                GitSyncResult.Failure("GitHub Sync is not configured."),
                null
            )
        }

        try {
            val targetBranch = config.branch.trim().ifBlank { "main" }
            val git = openOrCreateRepository(config)
            val credentials = UsernamePasswordCredentialsProvider(config.personalAccessToken, "")

            val pullResult = git.pull()
                .setRemote("origin")
                .setRemoteBranchName(targetBranch)
                .setCredentialsProvider(credentials)
                .call()

            if (!pullResult.isSuccessful) {
                val mergeResult = pullResult.mergeResult
                if (mergeResult != null && mergeResult.mergeStatus == MergeResult.MergeStatus.CONFLICTING) {
                    val conflicts = extractConflicts(git, mergeResult.conflicts.keys)
                    return@withContext Pair(GitSyncResult.Conflicts(conflicts), null)
                }
            }

            val importedSessions = mutableListOf<WorkSession>()
            val sessionsDir = File(localRepoDir, "sessions")
            if (sessionsDir.exists()) {
                sessionsDir.walkTopDown().filter { it.isFile && it.extension.lowercase() == "json" }.forEach { file ->
                    try {
                        val content = file.readText()
                        val session = GitSessionSerializer.deserializeSession(content)
                        importedSessions.add(session)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse session file: ${file.name}", e)
                    }
                }
            }

            val latestCommit = git.repository.resolve(Constants.HEAD)?.name
            GitSyncConfig.save(
                context,
                config.copy(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    lastSyncCommitHash = latestCommit
                )
            )

            val syncResult = GitSyncResult.Success(
                message = "Pull complete! Loaded ${importedSessions.size} sessions from GitHub.",
                sessionsPulled = importedSessions.size
            )
            Pair(syncResult, importedSessions)
        } catch (e: Exception) {
            Log.e(TAG, "Pull from GitHub failed", e)
            Pair(GitSyncResult.Failure("Pull failed: ${e.message ?: e.javaClass.simpleName}", e), null)
        }
    }

    /**
     * Resolves an individual file conflict by accepting either local or remote content.
     */
    suspend fun resolveConflict(
        conflict: GitConflictItem,
        choice: ConflictResolutionChoice,
        config: GitSyncConfig
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val git = Git.open(localRepoDir)
            val targetFile = File(localRepoDir, conflict.relativePath)
            targetFile.parentFile?.mkdirs()

            val resolvedContent = when (choice) {
                ConflictResolutionChoice.KEEP_LOCAL -> conflict.localRawJson
                ConflictResolutionChoice.KEEP_REMOTE -> conflict.remoteRawJson
            }

            targetFile.writeText(resolvedContent)
            git.add().addFilepattern(conflict.relativePath).call()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Finalizes conflict resolution: commits the resolved merge and pushes to remote.
     */
    suspend fun finishConflictResolution(config: GitSyncConfig): GitSyncResult = withContext(Dispatchers.IO) {
        try {
            val git = Git.open(localRepoDir)
            val credentials = UsernamePasswordCredentialsProvider(config.personalAccessToken, "")

            val targetBranch = config.branch.trim().ifBlank { "main" }

            git.commit()
                .setMessage("LevelUp: Resolved sync merge conflicts")
                .setAuthor(config.authorName, config.authorEmail)
                .call()

            git.push()
                .setRemote("origin")
                .setRefSpecs(RefSpec("refs/heads/$targetBranch:refs/heads/$targetBranch"))
                .setCredentialsProvider(credentials)
                .call()

            val latestCommit = git.repository.resolve(Constants.HEAD)?.name
            GitSyncConfig.save(
                context,
                config.copy(
                    lastSyncTimestamp = System.currentTimeMillis(),
                    lastSyncCommitHash = latestCommit
                )
            )

            GitSyncResult.Success("All conflicts resolved and synced to GitHub!")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to finish conflict resolution", e)
            GitSyncResult.Failure("Failed to push resolution: ${e.message}", e)
        }
    }

    /**
     * Aborts an active conflicted merge and resets working tree to HEAD.
     */
    suspend fun abortConflictedMerge(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (File(localRepoDir, ".git").exists()) {
                val git = Git.open(localRepoDir)
                git.reset().setMode(ResetCommand.ResetType.HARD).call()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun openOrCreateRepository(config: GitSyncConfig): Git {
        localRepoDir.mkdirs()
        val targetBranch = config.branch.trim().ifBlank { "main" }
        val git = if (File(localRepoDir, ".git").exists()) {
            Git.open(localRepoDir)
        } else {
            Git.init()
                .setDirectory(localRepoDir)
                .setInitialBranch(targetBranch)
                .call()
        }

        ensureBranch(git, targetBranch)

        val storedConfig: StoredConfig = git.repository.config
        storedConfig.setString("remote", "origin", "url", config.sanitizedRemoteUrl)
        storedConfig.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*")
        storedConfig.setString("branch", targetBranch, "remote", "origin")
        storedConfig.setString("branch", targetBranch, "merge", "refs/heads/$targetBranch")
        storedConfig.save()

        return git
    }

    private fun ensureBranch(git: Git, targetBranch: String) {
        val repo = git.repository
        val currentBranch = try { repo.branch } catch (e: Exception) { null }
        val headCommit = repo.resolve(Constants.HEAD)

        if (currentBranch != targetBranch) {
            if (headCommit == null) {
                try {
                    repo.updateRef(Constants.HEAD).link("refs/heads/$targetBranch")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to link unborn HEAD to $targetBranch: ${e.message}")
                }
            } else {
                val targetRef = repo.findRef("refs/heads/$targetBranch")
                try {
                    if (targetRef != null) {
                        git.checkout().setName(targetBranch).call()
                    } else if (!currentBranch.isNullOrBlank()) {
                        git.branchRename().setOldName(currentBranch).setNewName(targetBranch).call()
                    } else {
                        git.checkout().setName(targetBranch).setCreateBranch(true).call()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Branch switch/rename warning: ${e.message}")
                }
            }
        }
    }

    private fun extractConflicts(
        git: Git,
        conflictingPaths: Set<String>
    ): List<GitConflictItem> {
        val repo = git.repository
        val dirCache = repo.readDirCache()
        val items = mutableListOf<GitConflictItem>()

        for (path in conflictingPaths) {
            var oursBytes: ByteArray? = null
            var theirsBytes: ByteArray? = null

            for (i in 0 until dirCache.entryCount) {
                val entry: DirCacheEntry = dirCache.getEntry(i)
                if (entry.pathString == path) {
                    val loader = repo.open(entry.objectId)
                    when (entry.stage) {
                        DirCacheEntry.STAGE_2 -> oursBytes = loader.bytes
                        DirCacheEntry.STAGE_3 -> theirsBytes = loader.bytes
                    }
                }
            }

            val localJson = oursBytes?.let { String(it, Charsets.UTF_8) }
                ?: File(localRepoDir, path).takeIf { it.exists() }?.readText() ?: "{}"
            val remoteJson = theirsBytes?.let { String(it, Charsets.UTF_8) } ?: "{}"

            var sessionId: String? = null
            var sessionTimeFormatted: String? = null
            var localSummary = "Local content"
            var remoteSummary = "Cloud content"

            try {
                if (path.endsWith(".json")) {
                    val localSession = GitSessionSerializer.deserializeSession(localJson)
                    val remoteSession = GitSessionSerializer.deserializeSession(remoteJson)
                    sessionId = localSession.id
                    sessionTimeFormatted = localSession.formattedTimeRange()
                    localSummary = "${localSession.title} • ${localSession.formattedDuration()} (${localSession.category.displayName})"
                    remoteSummary = "${remoteSession.title} • ${remoteSession.formattedDuration()} (${remoteSession.category.displayName})"
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing conflict item preview: $path", e)
            }

            items.add(
                GitConflictItem(
                    relativePath = path,
                    sessionId = sessionId,
                    sessionTimeFormatted = sessionTimeFormatted,
                    localSummary = localSummary,
                    remoteSummary = remoteSummary,
                    localRawJson = localJson,
                    remoteRawJson = remoteJson
                )
            )
        }

        return items
    }

    private fun ensureRepoMetadata() {
        val metaDir = File(localRepoDir, ".levelup")
        metaDir.mkdirs()
        val metaFile = File(metaDir, "meta.json")
        if (!metaFile.exists()) {
            val json = JSONObject()
            json.put("schemaVersion", 1)
            json.put("generator", "LevelUp Android App")
            json.put("createdAt", Instant.now().toString())
            metaFile.writeText(json.toString(2))
        }

        val readmeFile = File(localRepoDir, "README.md")
        if (!readmeFile.exists()) {
            readmeFile.writeText(
                """
                # LevelUp Focus Data

                This repository contains focus/work sessions synced automatically by [LevelUp](https://github.com).

                ## Directory Structure
                - `sessions/YYYY/MM/`: Running work sessions partitioned chronologically by year and month
                - `.levelup/meta.json`: App and schema metadata
                """.trimIndent()
            )
        }
    }

    companion object {
        private const val TAG = "GitSyncManager"
    }
}
