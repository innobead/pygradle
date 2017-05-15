package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction


@GradleSupport
class PythonBuildTask : DefaultTask() {

    companion object {
        val distTypeSupports = listOf("bdist_wheel", "sdist", "bdist_wheel --universal")
    }

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    init {
        description = "Build Python package using setup.py"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonRuntimeTask::class.taskName))
            project.getTasksByName("build", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        when {
            project.file("setup.py").exists() -> {
                logger.lifecycle("Building package based on setup.py")

                var distType = "bdist_wheel"

                if (project.hasProperty("pyDistType")) {
                    distType = project.property("pyDistType").toString().apply {
                        if (this !in distTypeSupports) {
                            throw GradleException("Incorrect pyDistType ($this) property. Only support $distTypeSupports.")
                        }
                    }
                }

                project.exec {
                    it.workingDir(project.buildDir)
                    it.commandLine(listOf(
                            "bash", "-c",
                            "source $virtualenvDir/bin/activate; python setup.py $distType"
                    ))
                }.rethrowFailure()
            }

            else -> logger.lifecycle("Ignored package build, because setup.py is not found")
        }
    }

}