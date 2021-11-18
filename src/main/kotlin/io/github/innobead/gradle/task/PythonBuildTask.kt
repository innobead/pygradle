package io.github.innobead.gradle.task

import io.github.innobead.gradle.GradleSupport
import io.github.innobead.gradle.plugin.PythonPlugin
import io.github.innobead.gradle.plugin.pythonPluginExtension
import io.github.innobead.gradle.plugin.taskName
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonBuildTask : AbstractTask() {

    companion object {
        val distTypeSupports = listOf("sdist", "bdist_wheel", "bdist_wheel --universal")
    }

    @get:InputFiles
    val pythonSetupPyFiles by lazy {
        project.extensions.pythonPluginExtension.pythonSetupPyFiles
    }

    @get:Input
    val pypiRepoUrl by lazy {
        project.extensions.pythonPluginExtension.pypiRepoUrl
    }

    @get:Input
    val pypiRepoUsername by lazy {
        project.extensions.pythonPluginExtension.pypiRepoUsername
    }

    @get:Input
    val pypiRepoPassword by lazy {
        project.extensions.pythonPluginExtension.pypiRepoPassword
    }

    init {
        group = PythonPlugin.name
        description = "Build Python package using setup.py"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
            project.getTasksByName("build", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        val commands = mutableListOf("$pythonExecutable -m pip install wheel")

        if (!pythonSetupPyFiles!!.all { it.exists() }) {
            logger.lifecycle("Ignored package build, because setup.py is not found")
            return
        }

        pythonSetupPyFiles!!.forEach { setupFile ->
            logger.lifecycle("Building package based on setup.py")

            var distType = "bdist_wheel"

            if (project.hasProperty("pyDistType")) {
                distType = project.property("pyDistType").toString().apply {
                    if (this !in distTypeSupports) {
                        throw GradleException("Incorrect pyDistType ($this) property. Only support $distTypeSupports.")
                    }
                }
            }

            val uploadCommand = if (pypiRepoUrl != null && pypiRepoUsername != null && pypiRepoPassword != null) {
                File(System.getProperty("user.home"), ".pypirc").apply {
                    logger.lifecycle("Creating ${this.absolutePath}")
                    this.writeText(
                        """
        |[distutils]
        |index-servers=pypi-internal
        |
        |[pypi-internal]
        |repository=$pypiRepoUrl
        |username=$pypiRepoUsername
        |password=$pypiRepoPassword
""".trimMargin()
                    )
                }

                logger.lifecycle("Publishing package to $pypiRepoUrl")
                "upload -r pypi-internal"
            } else ""

            pythonBuildDir?.deleteRecursively()

            commands.add("$pythonExecutable $setupFile $distType --dist-dir=$pythonBuildDir $uploadCommand".trim())

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