package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPlugin
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonDependenciesTask : AbstractTask() {

    @get:Input
    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
    }

    @get:Input
    val keepBuildCached by lazy {
        project.extensions.pythonPluginExtension.keepBuildCached
    }

    @get:InputFile
    var copyLibsDir: File? = null

    init {
        group = PythonPlugin.name
        description = "Install Python dependencies"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonRuntimeTask::class.taskName))
        }
    }

    @TaskAction
    fun action() {
        if (!project.file("requirements.txt").exists()) {
            logger.lifecycle("Ignored to install dependencies, because requirements.txt not found")
            return
        }

        val f = File(project.buildDir, ".requirements")

        if (keepBuildCached && f.exists()) {
            logger.lifecycle("Ignored to install dependencies, because requirements flag is set")
            return
        }

        logger.lifecycle("Installing dependencies in requirements.txt ${f.exists()} ")

        project.exec {
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; " +
                            "$pythonExecutable -m pip install -r requirements.txt $pipOptions"
            ))
        }.rethrowFailure()

        val libsDir = File(project.buildDir, "libs")
        libsDir.mkdirs()

        logger.lifecycle("Downloading dependencies to $libsDir")

        project.exec {
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; " +
                            "$pythonExecutable -m pip install -I --prefix='$libsDir' -r requirements.txt $pipOptions".trim()
            ))
        }.rethrowFailure()

        if (copyLibsDir != null) {
            getPythonLibDir()?.apply {
                with(File(this, "site-packages")) {
                    copyRecursively(copyLibsDir!!, overwrite = true)
                }
            }
        }

        f.createNewFile()
    }

}