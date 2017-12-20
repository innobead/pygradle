package com.innobead.gradle.task

import com.innobead.gradle.GradleSupport
import com.innobead.gradle.plugin.pythonPluginExtension
import com.innobead.gradle.plugin.taskName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


@GradleSupport
class PythonGrpcTask : DefaultTask() {

    val protoSourceDirs by lazy {
        project.extensions.pythonPluginExtension.protoSourceDirs
    }

    val protoServiceProtoFiles by lazy {
        project.extensions.pythonPluginExtension.protoServiceProtoFiles
    }

    val protoCodeGeneratedDir by lazy {
        project.extensions.pythonPluginExtension.protoCodeGeneratedDir
    }

    val virtualenvDir by lazy {
        project.extensions.pythonPluginExtension.virtualenvDir
    }

    val pipOptions by lazy {
        project.extensions.pythonPluginExtension.pipOptions
    }

    init {
        description = "Build gRPC Python client code"

        project.afterEvaluate {
            dependsOn.add(project.tasks.getByName(PythonDependenciesTask::class.taskName))
            project.getTasksByName("test", false).firstOrNull()?.dependsOn(this)
        }
    }

    @TaskAction
    fun action() {
        logger.lifecycle("Building gRPC Python client code based on the proto files from ${protoSourceDirs}")

        val commands = listOf(
                "python -m pip install grpcio==1.7.0 grpcio-tools==1.7.0 $pipOptions",
                "python -m grpc_tools.protoc ${protoSourceDirs!!.map { "-I$it" }.joinToString(" ")} " +
                        "--python_out=$protoCodeGeneratedDir " +
                        "--grpc_python_out=$protoCodeGeneratedDir ${protoServiceProtoFiles!!.joinToString(" ") }"
        )

        protoCodeGeneratedDir!!.mkdirs()
        File(protoCodeGeneratedDir, "__init__.py").createNewFile()

        project.exec {
            it.isIgnoreExitValue = true
            it.commandLine(listOf(
                    "bash", "-c",
                    "source $virtualenvDir/bin/activate; ${commands.joinToString(";")}"
            ))
        }.rethrowFailure()
    }

}