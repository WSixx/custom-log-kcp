package br.com.brd.processor

import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.findAnnotation
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
class DebugLogTransformer(
    private val pluginContext: IrPluginContext,
    private val enabled: Boolean,
) : IrElementTransformerVoid() {

    @OptIn(
        FirIncompatiblePluginAPI::class,
        UnsafeDuringIrConstructionAPI::class
    )
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (!enabled) return super.visitSimpleFunction(declaration)


        val annotation =
            declaration.annotations.findAnnotation(FqName("br.com.brd.annotations.DebugLog"))
                ?: return super.visitSimpleFunction(declaration)

        // annotation args
        val originalBody = declaration.body as? IrBlockBody
        val messageExpr = annotation.arguments[0]

        // io.printLn
        val printlnCallableId = getCallableId()
        val printlnFunc =
            getPrintlnFunc(printlnCallableId) ?: return super.visitSimpleFunction(declaration)

        declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).irBlockBody {
            val concat = irConcat()
            val baseMessage = messageExpr ?: irString("Entering: ${declaration.name.asString()}")

            concat.addArgument(irString("("))
            concat.addArgument(baseMessage)
            concat.addArgument(irString(")"))

            // Build Code:
            // First Print
            +irCall(printlnFunc).apply { arguments.add(0, concat) }

            // Original Code
            originalBody?.statements?.forEach { statement -> +statement }

            // Last Print
            +irCall(printlnFunc).apply {
                arguments.add(0, irString("Exiting: ${declaration.name.asString()}"))
            }
        }

        return super.visitSimpleFunction(declaration)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun getPrintlnFunc(printlnCallableId: CallableId): IrSimpleFunctionSymbol? =
        pluginContext.referenceFunctions(printlnCallableId)
            .firstOrNull {
                val parameters = it.owner.parameters
                parameters.size == 1
            }

    private fun getCallableId(): CallableId =
        CallableId(FqName("kotlin.io"), Name.identifier("println"))

}