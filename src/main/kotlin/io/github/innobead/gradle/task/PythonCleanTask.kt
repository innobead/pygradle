package io.github.innobead.gradle.task

import io.github.innobead.gradle.GradleSupport
import io.github.innobead.gradle.plugin.PythonPlugin
import io.github.innobead.gradle.plugin.PythonPluginExtension
import org.gradle.api.tasks.TaskAction

@GradleSupport
class PythonCleanTask : AbstractTask() {

    init {
        group = PythonPlugin.name
        description = "Clean Python compiled things"

        project.afterEvaluate {
            project.getTasksByName("clean", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        val pythonPluginExtension = project.extensions.getByName("python") as PythonPluginExtension

        val sourceDirs = mutableListOf<String>()
        if (pythonPluginExtension.sourceDirs != null) {
            sourceDirs.addAll(pythonPluginExtension.sourceDirs?.map { it.absolutePath } ?: emptyList())
        }
        if (pythonPluginExtension.testReportDir != null) {
            sourceDirs.addAll(pythonPluginExtension.testSourceDirs?.map { it.absolutePath } ?: emptyList())
        }

        logger.lifecycle("Cleaning Python compiled things")
        if (sourceDirs.size == 0) {
            return
        }

        val commands = mutableListOf("find ${sourceDirs.joinToString(" ") { "'$it'" }} -name '*.pyc' -delete")
        project.exec {
            it.commandLine(
                listOf(
                    "bash", "-c",
                    commands.joinToString(";")
                )
            )
        }.rethrowFailure()
    }

}