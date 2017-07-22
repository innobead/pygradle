package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPluginExtension
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonTestTask : DefaultTask() {

    val testReportDir by lazy {
        project.extensions.pythonPluginExtension.testReportDir
    }

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
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

        testSourceDirs.forEach {
            File(it).walk().filter { it.isDirectory && "__pycache__" in it.name }.forEach {
                it.deleteRecursively()
            }
        }

        val commands = mutableListOf<String>()

        if (project.file("requirements-test.txt").exists()) {
            commands.add("pip install -r ${project.file("requirements-test.txt").absolutePath} $pipOptions".trim())
        }

        commands.addAll(
                listOf(
                        "pip install pytest pytest-cov",
                        "export PYTHONPATH='${sourceDirs.joinToString(":")}:\$PYTHONPATH'",
                        "pytest ${testSourceDirs.joinToString(" ")} " +
                                "--junit-xml=$testReportDir/junit-output.xml " +
                                "${sourceCovDirs!!.map { "--cov=${it.absolutePath}" }.joinToString(" ")} " +
                                "--cov-report term-missing " +
                                "--cov-report html --cov-report xml"
                )
        )

        project.exec {
            it.isIgnoreExitValue = true
            it.workingDir(testReportDir)
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
            ))
        }.rethrowFailure()
    }

}