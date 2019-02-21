package com.innobead.gradle.task

import com.innobead.gradle.plugin.pythonPluginExtension
import org.gradle.api.DefaultTask
import java.io.File

abstract class AbstractTask : DefaultTask() {

    val pythonDir by lazy {
        project.extensions.pythonPluginExtension.pythonDir
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

    fun getPythonLibDir(): File? {
        val pythonLibDirs = File(pythonDir, "lib").listFiles { f ->
            f.isDirectory
        }

        var pythonLibDir: File? = null
        for (pattern in listOf("2.*", "3.*").map { "python$it" }) {
            pythonLibDir = pythonLibDirs.first { pattern.toRegex().matches(it.name) }
            if (pythonLibDir != null) {
                break
            }
        }

        return pythonLibDir
    }
}