# Goals
Create a simple Python Gradle Plugin to compile, build, test and publish Python project in a sandbox. 

# Features
* Use Python virtualenv to test, build in sandbox.
* Use Python pip requirements for dependency management
  * requirements.txt for compile/runtime dependencies
  * requirements-test.txt for test compile/runtime/buildtime dependencies
* Use Python pytest library to test and do test coverage
* Build gRPC Python client code (grpc 1.41.0)
* Clean up pycache files before running tests
* Be able to publish build to PyPi repository.
* Support Python 2.* & 3.*

# Usages
Two commands are able to use.
## Build
`gradle build [-PpyDistType=["bdist_wheel", "sdist", "bdist_wheel --universal"]]`

## Test with coverage
`gradle test`

# Configurations

## build.gradle
```groovy
plugins {
  id "com.innobead.python" version "1.4.1"
}

python {
  sourceDirs = files('src')
  testSourceDirs = files('tests')

  protoSourceDirs = files('proto')
  protoServiceProtoFiles = files(new File('proto', 'service.proto'))
  protoCodeGeneratedDir = file('src/proto')

  pypiRepoUrl = 'https://pypi.python.org/simple/'
  pypiRepoUsername = 'admin'
  pypiRepoPassword = 'password'
  disableGrpc = false // set true to disable Grpc task
  keepBuildCached = false // set true to skip generation of build directory once already created
  pythonExecutable = "python" // adjust python executable name
}
```

## build.gradle.kts
```kotlin
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
```

# References
[com.innobead.python Gradle plugin](https://plugins.gradle.org/plugin/com.innobead.python)

# Notes
Thanks for **IntelliJ** great support!
