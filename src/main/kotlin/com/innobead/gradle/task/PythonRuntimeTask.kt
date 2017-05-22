package com.innobead.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.pythonPluginExtension


@GradleSupport
class PythonRuntimeTask : DefaultTask() {

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    val pythonDir by lazy {
        project.extensions.pythonPluginExtension.pythonDir
    }

    init {
        description = "Create Python sandbox (virtualenv)"
    }

    @TaskAction
    fun action() {
        val commands = mutableListOf<String>(
                """export PYTHONPATH="$pythonDir/lib/python2.7/site-packages:${'$'}PYTHONPATH""",
                """export PATH="$pythonDir/bin:${'$'}PATH"""
        )

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

        project.exec {
            it.isIgnoreExitValue = true
            it.executable("bash")
            it.args("-c", "which virtualenv > /dev/null 2>&1")
        }.exitValue.also {
            logger.lifecycle("Installing virtualenv")

            when (it) {
                0 -> {
                    commands.addAll(listOf(
                            "virtualenv $virtualenvDir"
                    ))
                }
                else -> {
                    commands.addAll(listOf(
                            "pip install virtualenv -I --prefix $pythonDir",
                            "$pythonDir/bin/virtualenv $virtualenvDir"
                    ))
                }
            }
        }

        logger.lifecycle("Creating an environment")
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