package leo.named.compiler

import leo.Stack
import leo.Type
import leo.first
import leo.named.typed.TypedExpression
import leo.named.typed.of
import leo.push
import leo.pushAll
import leo.stack

data class Types(val stack: Stack<Type>)
fun types(vararg types: Type) = Types(stack(*types))
fun Types.plus(type: Type) = stack.push(type).let(::Types)
fun Types.plus(types: Types): Types = stack.pushAll(types.stack).let(::Types)

fun Types.cast(type: Type): Type = castOrNull(type) ?: type

fun Types.castOrNull(type: Type): Type? =
	stack.first { it.choiceContains(type) }

fun Types.cast(typed: TypedExpression): TypedExpression =
	typed.expression.of(cast(typed.type))