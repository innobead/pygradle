package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPlugin
import com.innobead.gradle.plugin.pythonPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.io.NullOutputStream


@GradleSupport
class PythonRuntimeTask : AbstractTask() {

    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
    }

    val downloadUrls = listOf(
            "https://raw.githubusercontent.com/pypa/get-pip/master/get-pip.py",
            "https://bootstrap.pypa.io/get-pip.py"
    )

    init {
        group = PythonPlugin.name
        description = "Create Python sandbox (virtualenv)"
    }

    @TaskAction
    fun action() {
        val commands = mutableListOf<String>()

        var pipInstalled = isPipInstalled()

        if (!pipInstalled) {
            for (url in downloadUrls) {
                logger.lifecycle("Installing pip")

                commands.addAll(listOf(
                        "curl -OL $url",
                        "python get-pip.py -I --prefix $pythonDir",
                        "rm get-pip.py"
                ))

                logger.debug(commands.joinToString("\n"))

                val result = project.exec {
                    it.workingDir(project.extensions.pythonPluginExtension.tmpDir)
                    it.executable("bash")
                    it.environment(System.getenv())
                    it.args(listOf(
                            "-c",
                            commands.joinToString(";")
                    ))
                    it.isIgnoreExitValue = true
                }

                if (result.exitValue == 0) {
                    pipInstalled = true
                    break
                }
            }
        }

        if (!pipInstalled) {
            throw GradleException("Unable to install pip from $downloadUrls")
        }

        preparePythonEnv(commands)

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
        preparePythonEnv(commands)

        if (commands.isEmpty()) {
            throw GradleException("No Python installed in $pythonDir")
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

    private fun isPipInstalled(): Boolean {
        val result = project.exec {
            it.workingDir(project.extensions.pythonPluginExtension.tmpDir)
            it.executable("bash")
            it.environment(System.getenv())
            it.standardOutput = NullOutputStream.INSTANCE
            it.errorOutput = NullOutputStream.INSTANCE
            it.args(listOf(

                    "-c",
                    "python -m pip"
            ))
            it.isIgnoreExitValue = true
        }

        return result.exitValue == 0
    }

}
