package com.innobead.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class PythonPluginTest : SubjectSpek<Project>({

    subject {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.innobead.python")

        project
    }

    it("should have python plugin tasks") {
        assertTrue(PythonPlugin.builtinTasks.all { !subject.getTasksByName(it.taskName, false).isEmpty() })
    }

    it("should have extension variables") {
        assertNotNull(subject.extensions.getByName("python"))
    }

})