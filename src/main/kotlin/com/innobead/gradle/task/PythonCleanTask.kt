package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPluginExtension
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

@GradleSupport
class PythonCleanTask : DefaultTask() {

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    init {
        description = "Clean Python compiled things"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
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
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
            ))
        }.rethrowFailure()

    }

}