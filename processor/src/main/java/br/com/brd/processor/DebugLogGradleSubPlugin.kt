package br.com.brd.processor

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 *
 *
 *
 * created on 27/02/2026
 * @author Lucas Goncalves
 */
class DebugLogGradleSubPlugin : KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        // TODO: Build Variant android
        val isDebug = project.provider {
            "true"
        }

        return project.provider {
            listOf(SubpluginOption(key = "enabled", value = isDebug.get()))
        }
    }

    override fun getCompilerPluginId(): String = "br.com.brd.debuglog"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "br.com.brd",
        artifactId = "processor",
        version = "1.0.0"
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}