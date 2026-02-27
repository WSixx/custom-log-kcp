package br.com.brd.processor

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 *
 *
 *
 * created on 27/02/2026
 * @author Lucas Goncalves
 */
class DebugLogTransformer(private val pluginContext: IrPluginContext) : IrElementTransformerVoid() {

    @OptIn(
        FirIncompatiblePluginAPI::class, DeprecatedForRemovalCompilerApi::class,
        UnsafeDuringIrConstructionAPI::class
    )
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val hasDebugAnnotation =
            declaration.hasAnnotation(FqName("br.com.brd.annotations.DebugLog"))
        if (!hasDebugAnnotation) {
            // caso nao tenha apenas ignoramos
            return super.visitSimpleFunction(declaration)
        }

        val printlnCallableId = getCallabledId()
        val printlnFunc = getPrintlnFunc(printlnCallableId) ?: return super.visitSimpleFunction(declaration)

        fun createPrintCall(mensagem: String) = DeclarationIrBuilder(pluginContext, declaration.symbol).run {
            irCall(printlnFunc).apply {
                // Passamos o texto no índice 0
                putValueArgument(0, irString(mensagem))
            }
        }

        val originalBody = declaration.body as? IrBlockBody

        //  Vou construir o novo block de codigo com os prints
        val newCodeBlock = DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
            // First Inital Print
            +createPrintCall("Entering: ${declaration.name}")

            // then original block
            originalBody?.statements?.forEach { statement -> +statement }

            // last final print
            +createPrintCall("Exiting: ${declaration.name}")
        }

        declaration.body = newCodeBlock

        return super.visitSimpleFunction(declaration)
    }

    @OptIn(DeprecatedForRemovalCompilerApi::class, UnsafeDuringIrConstructionAPI::class)
    private fun getPrintlnFunc(printlnCallableId: CallableId): IrSimpleFunctionSymbol? =
        pluginContext.referenceFunctions(printlnCallableId)
            .firstOrNull { 
                val parameters = it.owner.valueParameters
                parameters.size == 1
            }

    private fun getCallabledId(): CallableId =
        CallableId(FqName("kotlin.io"), Name.identifier("println"))

}