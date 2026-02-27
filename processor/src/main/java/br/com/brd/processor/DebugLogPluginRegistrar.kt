package br.com.brd.processor

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class DebugLogPluginRegistrar(
    override val pluginId: String,
    override val supportsK2: Boolean
) : CompilerPluginRegistrar() {

    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration,
    ) {
        TODO("Not yet implemented")
    }
}