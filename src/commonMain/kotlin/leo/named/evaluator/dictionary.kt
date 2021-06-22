package leo.named.evaluator

import leo.Stack
import leo.Type
import leo.base.Seq
import leo.base.fold
import leo.fold
import leo.lineTo
import leo.linkOrNull
import leo.map
import leo.mapFirst
import leo.named.value.Value
import leo.push
import leo.reverse
import leo.script
import leo.scriptLine
import leo.seq
import leo.stack
import leo.throwScriptIfNull
import leo.type

data class Dictionary(val definitionStack: Stack<Definition>) { override fun toString() = scriptLine.toString() }

fun dictionary(vararg definitions: Definition): Dictionary = Dictionary(stack(*definitions))

val Dictionary.definitionSeq: Seq<Definition> get() =
	definitionStack.reverse.seq

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	fold(dictionary.definitionStack.reverse) { plus(it) }

fun Dictionary.rawBinding(type: Type): Binding =
	definitionStack
		.mapFirst { bindingOrNull(type) }
		.throwScriptIfNull { script(scriptLine, "value" lineTo script(type.scriptLine)) }

fun Dictionary.get(name: String): Value =
	value(type(name))

val Value.givenDictionary: Dictionary get() =
	dictionary().plus(givenDefinition).plus(linesDictionary)

val Value.linesDictionary: Dictionary get() =
	lineStack.map { definition }.let(::Dictionary)

fun Dictionary.bind(value: Value): Dictionary =
	plus(value.linesDictionary)

fun Dictionary.value(type: Type): Value =
	binding(type).value

fun Dictionary.binding(type: Type): Binding =
	rawBinding(type).resolve()

fun Binding.resolve(): Binding =
	when (this) {
		is RecursiveBinding -> recursive.recursiveDictionary.definitionStack.linkOrNull?.head
			?.let { recursive.binding.plus(it.set(recursive.baseDictionary.plusRecursive(recursive.recursiveDictionary))) }
			?: recursive.binding
		is ValueBinding -> this
		is FunctionBinding -> this
	}

fun Dictionary.plusRecursive(dictionary: Dictionary): Dictionary =
	let { base ->
		fold(dictionary.definitionSeq) { definition ->
			plus(definition.recursive(base, dictionary))
		}
	}
