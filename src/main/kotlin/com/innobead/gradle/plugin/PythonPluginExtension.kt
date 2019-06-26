package com.innobead.gradle.plugin

import com.innobead.gradle.GradleSupport
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import java.io.File

@GradleSupport
class PythonPluginExtension(val project: Project) {

    var sourceDirs: ConfigurableFileCollection? = null
        get() {
            return field ?: project.files(File("src"))
        }

    var testSourceDirs: ConfigurableFileCollection? = null
        get() {
            return field ?: project.files(File("tests"))
        }

    var tmpDir: File? = null
        get() {
            val dir = field ?: project.file(File(project.buildDir, "tmp"))
            dir.mkdirs()

            return dir
        }

    var virtualenvDir: File? = null
        get() {
            val dir = field ?: project.file(File(project.buildDir, "virtualenv"))
            dir.mkdirs()

            return dir
        }

    var pythonDir: File? = null
        get() {
            val dir = field ?: project.file(File(project.buildDir, "python"))
            dir.mkdirs()

            return dir
        }

    var pythonBuildDir: File? = null
        get() {
            val dir = field ?: project.file(File(project.buildDir, "python-build"))
            dir.mkdirs()

            return dir
        }

    var pythonSetupPyFiles: ConfigurableFileCollection? = null
        get() = field ?: project.files("setup.py")

    var testReportDir: File? = null
        get() {
            val dir = field ?: project.file(File(project.buildDir, "testReports"))
            dir.mkdirs()

            return dir
        }

    var protoSourceDirs: ConfigurableFileCollection? = null

    var protoServiceProtoFiles: ConfigurableFileCollection? = null

    var protoCodeGeneratedDir: File? = null
        get() {
            val dir = field ?: File(project.buildDir, "proto")
            dir.mkdirs()

            return dir
        }

    var keepBuildCached: Boolean = false

    var pypiRepoUrl: String? = null

    var pypiRepoUsername: String? = null

    var pypiRepoPassword: String? = null

    var pipOptions: String = ""

    var grpcVersion: String = "1.7.0"

    var disableGrpc: Boolean = false

    var pythonExecutable: String = "python"
}