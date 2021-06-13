package leo.named.compiler

import leo.Stack
import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.mapFirst
import leo.named.typed.TypedExpression
import leo.push
import leo.reverse
import leo.seq
import leo.stack

data class Dictionary(val definitionStack: Stack<Definition>) { override fun toString() = scriptLine.toString() }
fun dictionary(vararg definitions: Definition) = Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plusName(typeLine: TypeLine): Dictionary =
	plus(typeLine.nameDefinition)

fun Dictionary.plusNames(structure: TypeStructure): Dictionary =
	fold(structure.lineStack.reverse.seq) { plusName(it) }

fun Dictionary.bindingOrNull(structure: Type): Binding? =
	definitionStack.mapFirst { bindingOrNull(structure) }

fun Dictionary.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
	bindingOrNull(typedExpression.type)?.resolve(typedExpression)
