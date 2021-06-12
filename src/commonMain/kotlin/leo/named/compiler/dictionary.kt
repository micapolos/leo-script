package leo.named.compiler

import leo.Stack
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.mapFirst
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.push
import leo.reverse
import leo.seq
import leo.stack

data class Dictionary(val definitionStack: Stack<Definition>)
fun dictionary(vararg definitions: Definition) = Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(typeLine: TypeLine): Dictionary =
	plus(typeLine.definition())

fun Dictionary.plus(structure: TypeStructure): Dictionary =
	fold(structure.lineStack.reverse.seq) { plus(it) }

fun Dictionary.bindingOrNull(structure: TypeStructure): Binding? =
	definitionStack.mapFirst { bindingOrNull(structure) }

fun <T> Dictionary.resolveOrNull(typedStructure: TypedStructure<T>): TypedExpression<T>? =
	bindingOrNull(typedStructure.typeStructure)?.resolve(typedStructure)
