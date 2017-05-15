package com.innobead.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ExtensionContainer
import kotlin.reflect.KClass


val KClass<out DefaultTask>.taskName: String
    get() = this.simpleName!!.decapitalize().replace("[t|T]ask".toRegex(), "")

val ExtensionContainer.pythonPluginExtension: PythonPluginExtension
    get() = this.getByName("python") as PythonPluginExtension
