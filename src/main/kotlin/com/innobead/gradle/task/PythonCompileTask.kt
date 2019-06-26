package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPlugin
import com.innobead.gradle.plugin.PythonPluginExtension
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


@GradleSupport
class PythonCompileTask : AbstractTask() {

    init {
        group = PythonPlugin.name
        description = "Compile Python code in source folders"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
        }
    }

    @TaskAction
    fun action() {
        val pythonPluginExtension = project.extensions.getByName("python") as PythonPluginExtension
        val sourceDirs = (pythonPluginExtension.sourceDirs!!.toList() + project.projectDir).map { it.absolutePath }

        logger.lifecycle("Compiling python scripts in source folders ($sourceDirs)")

        val commands = mutableListOf("$pythonExecutable -m compileall -f ${sourceDirs.joinToString(" ")}")
        project.exec {
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
            ))
        }.rethrowFailure()

    }

}