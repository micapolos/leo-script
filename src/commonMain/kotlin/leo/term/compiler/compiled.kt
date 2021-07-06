package leo.term.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.actionName
import leo.base.fold
import leo.base.notNullOrError
import leo.base.reverse
import leo.doName
import leo.doingName
import leo.functionLineTo
import leo.getName
import leo.isEmpty
import leo.lineSeq
import leo.makeName
import leo.matchInfix
import leo.onlyNameOrNull
import leo.performName
import leo.quoteName
import leo.term.fn
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.do_
import leo.term.typed.invoke
import leo.term.typed.lineTo
import leo.term.typed.make
import leo.term.typed.plus
import leo.term.typed.staticTypedTerm
import leo.term.typed.typed
import leo.typeLine

data class Compiled<V>(
	val context: Context<V>,
	val typedTerm: TypedTerm<V>)

fun <V> Compiled<V>.set(typedTerm: TypedTerm<V>): Compiled<V> =
	copy(typedTerm = typedTerm)

fun <V> Compiled<V>.plus(script: Script): Compiled<V> =
	fold(script.lineSeq.reverse) { plus(it) }

fun <V> Compiled<V>.plus(scriptLine: ScriptLine): Compiled<V> =
	when (scriptLine) {
		is FieldScriptLine -> plus(scriptLine.field)
		is LiteralScriptLine -> plus(scriptLine.literal)
	}

fun <V> Compiled<V>.plus(literal: Literal): Compiled<V> =
	plus(typed(context.environment.literalFn(literal), literal.typeLine))

fun <V> Compiled<V>.plus(field: ScriptField): Compiled<V> =
	null
		?: plusSpecialOrNull(field)
		?: plusNamed(field)

fun <V> Compiled<V>.plusNamed(field: ScriptField): Compiled<V> =
	plus(field.name lineTo context.typedTerm(field.rhs))

fun <V> Compiled<V>.plusSpecialOrNull(field: ScriptField): Compiled<V>? =
	when (field.name) {
		actionName -> plusAction(field.rhs)
		//compileName -> plusCompile(field.rhs)
		doName -> plusDo(field.rhs)
		getName -> plusGet(field.rhs)
		makeName -> plusMake(field.rhs)
		performName -> plusPerform(field.rhs)
		quoteName -> plusQuote(field.rhs)
		else -> null
	}

fun <V> Compiled<V>.plusAction(script: Script): Compiled<V> =
	script.matchInfix { lhs, name, rhs ->
		when (name) {
			doingName -> context.type(lhs).let { type ->
				context.plus(binding(given(type))).typedTerm(rhs).let { typedTerm ->
					plus(typed(fn(typedTerm.v), type functionLineTo typedTerm.t))
				}
			}
			else -> null
		}
	}.notNullOrError("parse error action")

fun <V> Compiled<V>.plusDo(script: Script): Compiled<V> =
	set(typedTerm.do_(context.plus(binding(given(typedTerm.t))).typedTerm(script)))

fun <V> Compiled<V>.plusGet(script: Script): Compiled<V> =
	script.get.let { get ->
		set(
			if (typedTerm.t.isEmpty) context.scope.invoke(get)
			else typedTerm.invoke(get))
	}

fun <V> Compiled<V>.plusMake(script: Script): Compiled<V> =
	script.onlyNameOrNull.notNullOrError("syntax make").let { name ->
		set(typedTerm.make(name))
	}

fun <V> Compiled<V>.plusPerform(script: Script): Compiled<V> =
	set(context.typedTerm(script).invoke(typedTerm))

fun <V> Compiled<V>.plusQuote(script: Script): Compiled<V> =
	if (!typedTerm.t.isEmpty) error("$typedTerm not empty")
	else set(context.environment.staticTypedTerm(script))

fun <V> Compiled<V>.plus(typedLine: TypedLine<V>): Compiled<V> =
	Compiled(context, context.resolve(typedTerm.plus(typedLine)))
