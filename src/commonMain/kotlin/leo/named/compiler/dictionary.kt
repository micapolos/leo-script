package leo.named.compiler

import leo.ChoiceType
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeStructure
import leo.base.fold
import leo.fold
import leo.lineSeq
import leo.mapFirst
import leo.named.typed.TypedExpression
import leo.push
import leo.reverse
import leo.stack

data class Dictionary(val definitionStack: Stack<Definition>) {
  override fun toString() = scriptLine.toString()
}

fun dictionary(vararg definitions: Definition) = Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
  definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(dictionary: Dictionary): Dictionary =
  fold(dictionary.definitionStack.reverse) { plus(it) }

fun Dictionary.bindingOrNull(structure: Type): Binding? =
  definitionStack.mapFirst { bindingOrNull(structure) }

fun Dictionary.resolve(typedExpression: TypedExpression): TypedExpression =
  resolveOrNull(typedExpression) ?: typedExpression

fun Dictionary.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
  bindingOrNull(typedExpression.type)?.resolve(typedExpression)

val Type.doDictionary: Dictionary
  get() =
    dictionary().plus(givenDefinition).plus(bindDictionary)

val Type.bindDictionary: Dictionary
  get() =
    // TODO: Add "content"
    when (this) {
      is ChoiceType -> dictionary()
      is StructureType -> structure.bindDictionary
    }

val TypeStructure.bindDictionary: Dictionary
  get() =
    dictionary().fold(lineSeq) { plus(it.bindDefinition) }
