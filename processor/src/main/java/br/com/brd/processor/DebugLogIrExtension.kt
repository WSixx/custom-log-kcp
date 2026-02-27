package br.com.brd.processor

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

/**
 *
 *
 *
 * created on 27/02/2026
 * @author Lucas Goncalves
 */
class DebugLogIrExtension : IrGenerationExtension {

    /**
     * Vai receber toda arvore do app para ver e modificar
     * usa o pattern Visitator
     */
    override fun generate(
        moduleFragment: IrModuleFragment, // raiz de toda arvore do codigo
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.transformChildrenVoid(DebugLogTransformer(pluginContext))
    }
}