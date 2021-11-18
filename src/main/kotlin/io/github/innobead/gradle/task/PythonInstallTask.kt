package io.github.innobead.gradle.task

import io.github.innobead.gradle.GradleSupport
import io.github.innobead.gradle.plugin.PythonPlugin
import io.github.innobead.gradle.plugin.taskName
import org.gradle.api.tasks.TaskAction


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
                it.commandLine(
                    listOf(
                        "bash", "-c",
                        "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
                    )
                )
            }.rethrowFailure()
        }
    }

}