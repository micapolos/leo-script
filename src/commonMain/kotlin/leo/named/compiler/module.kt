package leo.named.compiler

import leo.Stack
import leo.fold
import leo.named.expression.Binding
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.reverse

data class Module(
	val privateContext: Context,
	val publicContext: Context)

val Context.module get() =  Module(privateContext = this, publicContext = context())
fun module() = context().module

fun Module.plus(definition: Definition) =
	Module(privateContext.plus(definition), publicContext.plus(definition))

fun Module.plus(dictionary: Dictionary) =
	Module(privateContext.plus(dictionary), publicContext.plus(dictionary))

fun Module.plusPrivate(definition: Definition) =
	Module(privateContext.plus(definition), publicContext)

fun Module.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
	privateContext.resolveOrNull(typedExpression)

fun Module.scopePlus(binding: Binding): Module =
	Module(privateContext.scopePlus(binding), publicContext.scopePlus(binding))

fun Module.bind(typedLine: TypedLine): Module =
	Module(privateContext.bind(typedLine), publicContext.bind(typedLine))

fun Module.bind(typedLineStack: Stack<TypedLine>): Module =
	fold(typedLineStack.reverse) { bind(it) }

