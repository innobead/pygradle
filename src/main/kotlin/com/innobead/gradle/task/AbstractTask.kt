package com.innobead.gradle.task

import com.innobead.gradle.plugin.pythonPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import java.io.File

abstract class AbstractTask : DefaultTask() {

    @get:InputDirectory
    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    @get:InputDirectory
    val pythonDir by lazy {
        project.extensions.pythonPluginExtension.pythonDir
    }

    @get:InputDirectory
    val pythonBuildDir by lazy {
        project.extensions.pythonPluginExtension.pythonBuildDir
    }

    @get:Input
    val pythonExecutable by lazy {
        project.extensions.pythonPluginExtension.pythonExecutable
    }

    fun preparePythonEnv(commands: MutableList<String>) {
        commands.clear()

        var pythonLibDir = getPythonLibDir()
        if (pythonLibDir != null) {
            pythonLibDir = File(pythonLibDir, "site-packages")
            commands.add("""export PYTHONPATH="${pythonLibDir.canonicalPath}":${'$'}PYTHONPATH""")
        }

        commands.add("""export PATH="$pythonDir/bin":${'$'}PATH""")
    }

    @Optional
    @InputDirectory
    fun getPythonLibDir(): File? {
        var pythonLibDir: File? = null

        for (pattern in listOf("2.*", "3.*").map { "python$it" }) {
            pythonLibDir = File(pythonDir, "lib").listFiles { f ->
                f.isDirectory
            }?.find {
                pattern.toRegex().matches(it.name)
            }

            if (pythonLibDir != null) {
                break
            }
        }

        return pythonLibDir
    }
}