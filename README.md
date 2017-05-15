[![Travis CI](https://travis-ci.org/innobead/pygradle.svg?branch=master)](https://travis-ci.org/innobead/pygradle)

# Goals
Create a simple Ptyhon Gradle Plugin to build and test Python project in a sandbox. 

# Features
* Use Python virtualenv to build and test in sandbox.
* Use Python pytest library to test and do test coverage.

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
        classpath "gradle.plugin.com.innobead:gradle-python-plugin:1.0.6"
    }
}

apply plugin: "com.innobead.python"

python.sourceDirs = files('src')
python.testSourceDirs = files('tests')
```

# References
[com.innobead.python Gradle plugin](https://plugins.gradle.org/plugin/com.innobead.python)
