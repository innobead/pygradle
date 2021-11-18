import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.innobead"
version = "1.4.1"

plugins {
    kotlin("jvm") version "1.5.31"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version  "0.16.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.31"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation(kotlin("maven-allopen"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.4.0")
}

allOpen {
    annotation("io.github.innobead.gradle.GradleSupport")
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
            implementationClass = "io.github.innobead.gradle.plugin.PythonPlugin"
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.apiVersion = "1.4"
compileKotlin.kotlinOptions.jvmTarget = "11"

afterEvaluate {
    val publishKey = System.getenv("GRADLE_PUBLISH_KEY")
    val publishSecret = System.getenv("GRADLE_PUBLISH_SECRET")

    if (publishKey != null && publishSecret != null) {
        System.setProperty("gradle.publish.key", publishKey)
        System.setProperty("gradle.publish.secret", publishSecret)
    }
}
