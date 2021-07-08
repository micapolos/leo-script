package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.fold
import leo.isEmpty
import leo.lineStack
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.reverse
import leo.script
import leo.term.Term
import leo.term.fn
import leo.term.invoke
import leo.term.typed.Typed
import leo.term.typed.TypedTerm
import leo.type

data class SwitchCompiler<V>(
	val context: Context<V>,
	val remainingCaseStack: Stack<TypeLine>,
	val term: Term<V>,
	val typeOrNull: Type?)

fun <V> SwitchCompiler<V>.plus(script: Script): SwitchCompiler<V> =
	fold(script.lineStack.reverse) { plus(it) }

fun <V> SwitchCompiler<V>.plus(line: ScriptLine): SwitchCompiler<V> =
	when (line) {
		is FieldScriptLine -> plus(line.field)
		is LiteralScriptLine -> compileError("switch" lineTo script())
	}

fun <V> SwitchCompiler<V>.plus(field: ScriptField): SwitchCompiler<V> =
	remainingCaseStack.linkOrNull
		.let { remainingCaseStackLink ->
			if (remainingCaseStackLink == null) compileError("switch" lineTo script())
			else if (remainingCaseStackLink.head.name != field.name) compileError("switch" lineTo script())
			else context.plus(binding(given(type(remainingCaseStackLink.head)))).typedTerm(field.rhs).let { caseTypedTerm ->
				caseTypedTerm.t.also {
					if (typeOrNull != null && typeOrNull != it) {
						compileError("type" lineTo script())
					}
				}
				.let { newType ->
					term.invoke(fn(caseTypedTerm.v)).let { newTerm ->
						SwitchCompiler(context, remainingCaseStackLink.tail, newTerm, newType)
					}
				}
			}
		}

val <V> SwitchCompiler<V>.typedTerm: TypedTerm<V> get() =
	if (typeOrNull == null) compileError("type" lineTo script())
	else if (!remainingCaseStack.isEmpty) compileError("exha" lineTo script())
	else Typed(term, typeOrNull)
