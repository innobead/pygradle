plugins {
    id("com.innobead.python") version "1.4.1"
}

python {
    sourceDirs = files("src")
    testSourceDirs = files("tests")

    protoSourceDirs = files("proto")
    protoServiceProtoFiles = files("proto", "service.proto")
    protoCodeGeneratedDir = file("src/proto")

    pypiRepoUrl = "https://pypi.python.org/simple/"
    pypiRepoUsername = "admin"
    pypiRepoPassword = "password"
    disableGrpc = false // set true to disable Grpc task
    keepBuildCached = false // set true to skip generation of build directory once already created
    pythonExecutable = "python" // adjust python executable name
}
