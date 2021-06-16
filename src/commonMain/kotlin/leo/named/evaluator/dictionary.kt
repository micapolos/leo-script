package leo.named.evaluator

import leo.Stack
import leo.Type
import leo.fold
import leo.get
import leo.lineTo
import leo.linkOrNull
import leo.map
import leo.mapFirst
import leo.named.expression.Body
import leo.named.expression.Expression
import leo.named.expression.ExpressionBody
import leo.named.expression.FnBody
import leo.named.value.Value
import leo.push
import leo.reverse
import leo.script
import leo.scriptLine
import leo.stack
import leo.throwScriptIfNull
import leo.type

data class Dictionary(val definitionStack: Stack<Definition>) { override fun toString() = scriptLine.toString() }

fun dictionary(vararg definitions: Definition): Dictionary = Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	fold(dictionary.definitionStack.reverse) { plus(it) }

fun Dictionary.value(body: Body): Value =
	when (body) {
		is ExpressionBody -> value(body.expression)
		is FnBody -> body.valueFn(this)
	}

fun Dictionary.value(expression: Expression): Value =
	expression.valueEvaluation.get(this)

fun Dictionary.rawBinding(type: Type): Binding =
	definitionStack
		.mapFirst { bindingOrNull(type) }
		.throwScriptIfNull { script(scriptLine, "value" lineTo script(type.scriptLine)) }

fun Dictionary.get(name: String): Value =
	value(type(name))

val Value.dictionary: Dictionary get() =
	lineStack.map { definition }.let(::Dictionary)

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
	fold(dictionary.definitionStack.reverse) { definition ->
		plus(definition.recursive(this, dictionary))
	}
