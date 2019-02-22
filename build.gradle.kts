plugins {
    kotlin("jvm") version "1.3.21"
    `java-gradle-plugin`
    java
    id("com.gradle.plugin-publish") version "0.10.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.21"
    maven
}

group = "com.innobead"
version = "1.3.2"

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile(gradleApi())

    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))

    testCompile(kotlin("test"))
    testCompile("com.nhaarman:mockito-kotlin:1.4.0")
    testCompile(kotlin("maven-allopen"))

    testRuntime("org.junit.platform:junit-platform-launcher:1.4.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.0")
    testRuntime("org.junit.vintage:junit-vintage-engine:5.4.0")
}

allOpen {
    annotation("com.innobead.gradle.GradleSupport")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

gradlePlugin {
    plugins {
        create("pythonPlugin") {
            id = "$group.python"
            displayName = "pygradle, Python plugin"
            description = "A Python plugin for building, testing, dependency management and popular frameworks (gRPC, protobuf, ...) supported"
            implementationClass = "com.innobead.gradle.plugin.PythonPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/innobead/pygradle"
    vcsUrl = "https://github.com/innobead/pygradle"
    tags = listOf("python")

    (plugins) {
        "pythonPlugin" {}
    }
}

afterEvaluate {
    val publishKey = System.getenv("GRADLE_PUBLISH_KEY")
    val publishSecret = System.getenv("GRADLE_PUBLISH_SECRET")

    if (publishKey != null && publishSecret != null) {
        System.setProperty("gradle.publish.key", publishKey)
        System.setProperty("gradle.publish.secret", publishSecret)
    }
}
