package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.pythonPluginExtension
import org.gradle.api.BuildCancelledException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonRuntimeTask : DefaultTask() {

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    val pythonDir by lazy {
        project.extensions.pythonPluginExtension.pythonDir
    }

    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
    }

    init {
        description = "Create Python sandbox (virtualenv)"
    }

    @TaskAction
    fun action() {
        val commands = mutableListOf<String>()

        commands.add("""export PATH="$pythonDir/bin":${'$'}PATH""")

        project.exec {
            it.isIgnoreExitValue = true
            it.executable("bash")
            it.args("-c", "which pip > /dev/null 2>&1")
        }.exitValue.also {
            logger.lifecycle("Installing pip")

            if (it != 0) {
                commands.addAll(listOf(
                        "curl -OL https://bootstrap.pypa.io/get-pip.py",
                        "python get-pip.py -I --prefix $pythonDir",
                        "rm get-pip.py"
                ))
            }
        }

        val commandToCreateVirtualEnv = project.exec {
            it.isIgnoreExitValue = true
            it.executable("bash")
            it.args("-c", "which virtualenv > /dev/null 2>&1")
        }.exitValue.let {
            when (it) {
                0 -> {
                    "virtualenv $virtualenvDir"
                }
                else -> {
                    commands.addAll(listOf(
                            "python -m pip install virtualenv -I --prefix $pythonDir $pipOptions".trim()

                    ))

                    "$pythonDir/bin/virtualenv $virtualenvDir"
                }
            }
        }

        logger.lifecycle("Installing virtualenv")
        logger.debug(commands.joinToString("\n"))

        project.exec {
            it.workingDir(project.extensions.pythonPluginExtension.tmpDir)
            it.executable("bash")
            it.environment(System.getenv())
            it.args(listOf(
                    "-c",
                    commands.joinToString(";")
            ))
        }.rethrowFailure()

        logger.lifecycle("Creating a virtual environment")

        commands.clear()

        listOf("2.7", "3.6").map { File(pythonDir, "lib/python$it/site-packages") }.find {
            it.exists()
        }?.apply {
            commands.add(
                    """export PYTHONPATH="${this.canonicalPath}":${'$'}PYTHONPATH"""
            )
        }

        if (commands.isEmpty()) {
            throw BuildCancelledException("No Python installed in $pythonDir")
        }

        commands.add(commandToCreateVirtualEnv)

        project.exec {
            it.workingDir(project.extensions.pythonPluginExtension.tmpDir)
            it.executable("bash")
            it.environment(System.getenv())
            it.args(listOf(
                    "-c",
                    commands.joinToString(";")
            ))
        }.rethrowFailure()
    }

}
