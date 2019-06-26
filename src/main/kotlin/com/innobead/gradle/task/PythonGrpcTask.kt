package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.PythonPlugin
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonGrpcTask : AbstractTask() {

    @get:InputFiles
    val protoSourceDirs by lazy {
        project.extensions.pythonPluginExtension.protoSourceDirs
    }

    @get:InputFiles
    val protoServiceProtoFiles by lazy {
        project.extensions.pythonPluginExtension.protoServiceProtoFiles
    }

    @get:InputFile
    val protoCodeGeneratedDir by lazy {
        project.extensions.pythonPluginExtension.protoCodeGeneratedDir
    }

    @get:Input
    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
    }

    @get:Input
    val grpcVersion by lazy {
        project.extensions.pythonPluginExtension.grpcVersion
    }

    @get:Input
    val disableGrpc by lazy {
        project.extensions.pythonPluginExtension.disableGrpc
    }


    init {
        group = PythonPlugin.name
        description = "Build gRPC Python client code"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
            project.getTasksByName("test", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        if (disableGrpc) {
            logger.lifecycle("Skipping gRPC task due to the config")
        } else {
            logger.lifecycle("Building gRPC Python client code based on the proto files from ${protoSourceDirs}")

            val commands = listOf(
                    "$pythonExecutable -m pip install grpcio==$grpcVersion grpcio-tools==$grpcVersion $pipOptions",
                    "$pythonExecutable -m grpc_tools.protoc ${protoSourceDirs!!.map { "-I$it" }.joinToString(" ")} " +
                            "--python_out=$protoCodeGeneratedDir " +
                            "--grpc_python_out=$protoCodeGeneratedDir ${protoServiceProtoFiles!!.joinToString(" ")}"
            )

            protoCodeGeneratedDir!!.mkdirs()
            File(protoCodeGeneratedDir, "__init__.py").createNewFile()

            project.exec {
                it.isIgnoreExitValue = true
                it.commandLine(listOf(
                        "bash", "-c",
                        "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
                ))
            }
        }
    }

}