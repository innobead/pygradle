package com.innobead.gradle.plugin

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project


@GradleSupport
class PythonPlugin : Plugin<Project> {

    companion object {
        val name = "python"

        val builtinTasks = listOf(
                PythonRuntimeTask::class,
                PythonDependenciesTask::class,
                PythonCompileTask::class,
                PythonBuildTask::class,
                PythonTestTask::class,
                PythonGrpcTask::class,
                PythonCleanTask::class
        )
    }

    override fun apply(project: Project?) {
        with(project!!) {
            apply(mapOf("plugin" to "base"))

            extensions.create("python", PythonPluginExtension::class.java, this)

            logger.debug("Creating $builtinTasks tasks")
            builtinTasks.forEach {
                project.tasks.create(it.taskName, it.java)
            }

            afterEvaluate {
                if (getTasksByName("test", false).isEmpty()) {
                    val testTask = task(
                            mapOf("description" to "Unit testing"),
                            "test"
                    ).dependsOn(getTasksByName("pythonTest", false).first())

                    getTasksByName("pythonBuild", false).firstOrNull()?.dependsOn(testTask)
                }
            }
        }
    }

}
