package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPlugin
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonInstallTask : AbstractTask() {

    init {
        group = PythonPlugin.name
        description = "Install Python package"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonBuildTask::class.taskName))
            project.getTasksByName("install", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        logger.lifecycle("Installing package from $pythonBuildDir")

        pythonBuildDir?.listFiles { _, name ->
            name.endsWith(".whl")
        }?.forEach {
            logger.lifecycle("Installing $it")

            val commands = listOf("pip install $it".trim())

            project.exec {
                it.commandLine(listOf(
                        "bash", "-c",
                        "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
                ))
            }.rethrowFailure()
        }
    }

}