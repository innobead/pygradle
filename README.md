[![Travis CI](https://travis-ci.org/innobead/pygradle.svg)](https://travis-ci.org/innobead/pygradle)

# Goals
Create a simple Python Gradle Plugin to compile, build, test and publish Python project in a sandbox. 

# Features
* Use Python virtualenv to test, build in sandbox.
* Use Python pip requirements for dependency management
  * requirements.txt for compile/runtime dependencies
  * requirements-test.txt for test compile/runtime/buildtime dependencies
* Use Python pytest library to test and do test coverage
* Build gRPC Python client code (grpc 1.7.0)
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
```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.innobead:gradle-python-plugin:1.3.5"
    }
}

apply plugin: "com.innobead.python"

python {
    sourceDirs = files('src')
    testSourceDirs = files('tests')
    
    protoSourceDirs = files('proto')
    protoServiceProtoFiles = files(new File('proto', 'service.proto'))
    protoCodeGeneratedDir = file('src/proto')
    
    pypiRepoUrl = 'https://pypi.python.org/simple/'
    pypiRepoUsername = 'admin'
    pypiRepoPassword = 'admin123'
}

```

# References
[com.innobead.python Gradle plugin](https://plugins.gradle.org/plugin/com.innobead.python)
