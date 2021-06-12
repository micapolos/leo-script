package leo.named.compiler

import leo.Stack
import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.mapFirst
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.push
import leo.reverse
import leo.seq
import leo.stack
import leo.type

data class Dictionary(val definitionStack: Stack<Definition>)
fun dictionary(vararg definitions: Definition) = Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(typeLine: TypeLine): Dictionary =
	plus(typeLine.definition())

fun Dictionary.plusLines(structure: TypeStructure): Dictionary =
	fold(structure.lineStack.reverse.seq) { plus(it) }

fun Dictionary.bindingOrNull(structure: Type): Binding? =
	definitionStack.mapFirst { bindingOrNull(structure) }

fun <T> Dictionary.resolveOrNull(typedExpression: TypedExpression<T>): TypedLine<T>? =
	bindingOrNull(typedExpression.typeStructure.type)?.resolve(typedExpression)
