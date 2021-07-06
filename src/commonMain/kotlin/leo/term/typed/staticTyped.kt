package leo.term.typed

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.reverse
import leo.lineSeq
import leo.term.compiler.Environment
import leo.typeLine

fun <V> Environment<V>.staticTypedTerm(script: Script): TypedTerm<V> =
	typedTerm<V>().fold(script.lineSeq.reverse) { plus(staticTypedLine(it)) }

fun <V> Environment<V>.staticTypedLine(scriptLine: ScriptLine): TypedLine<V> =
	when (scriptLine) {
		is FieldScriptLine -> staticTypedLine(scriptLine.field)
		is LiteralScriptLine -> typed(literalFn(scriptLine.literal), scriptLine.literal.typeLine)
	}

fun <V> Environment<V>.staticTypedLine(scriptField: ScriptField): TypedLine<V> =
	scriptField.name lineTo staticTypedTerm(scriptField.rhs)