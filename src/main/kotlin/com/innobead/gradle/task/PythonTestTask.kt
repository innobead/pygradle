package com.innobead.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPluginExtension
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName


@GradleSupport
class PythonTestTask : DefaultTask() {

    val testReportDir by lazy {
        project.extensions.pythonPluginExtension.testReportDir
    }

    init {
        description = "Python Unit test (pytest)"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
            project.getTasksByName("test", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        logger.lifecycle("Unit testing (pytest only)")

        val pythonPluginExtension = project.extensions.getByName("python") as PythonPluginExtension
        val sourceDirs = (pythonPluginExtension.sourceDirs!!.toList() + project.projectDir).map { it.absolutePath }
        val testSourceDirs = pythonPluginExtension.testSourceDirs!!.map { it.absolutePath }
        val sourceCovDirs = pythonPluginExtension.sourceDirs

        val runtimeTask = project.tasks.getByName(PythonRuntimeTask::class.taskName) as PythonRuntimeTask
        val commands = listOf(
                "pip install -r ${project.file("requirements-test.txt").absolutePath}",
                "pip install pytest pytest-cov",
                "export PYTHONPATH='${sourceDirs.joinToString(":")}:\$PYTHONPATH'",
                "pytest ${testSourceDirs.joinToString(" ")} " +
                        "--junit-xml=$testReportDir/junit-output.xml " +
                        "${sourceCovDirs!!.map { "--cov=${it.absolutePath}" }.joinToString(" ")} " +
                        "--cov-report term-missing " +
                        "--cov-report html --cov-report xml"
        )

        project.exec {
            it.setWorkingDir(testReportDir)
            it.commandLine(listOf(
                    "bash", "-c",
                    "source ${runtimeTask.virtualenvDir}/bin/activate; ${commands.joinToString(";")}"
            ))
        }.rethrowFailure()
    }

}