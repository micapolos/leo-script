package leo.term.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.notNullOrError
import leo.base.reverse
import leo.doName
import leo.getName
import leo.isEmpty
import leo.lineSeq
import leo.makeName
import leo.onlyNameOrNull
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.do_
import leo.term.typed.getOrNull
import leo.term.typed.lineTo
import leo.term.typed.make
import leo.term.typed.plus

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
	plus(context.environment.literalFn(literal))

fun <V> Compiled<V>.plus(field: ScriptField): Compiled<V> =
	null
		?: plusSpecialOrNull(field)
		?: plusNamed(field)

fun <V> Compiled<V>.plusNamed(field: ScriptField): Compiled<V> =
	plus(field.name lineTo context.typedTerm(field.rhs))

fun <V> Compiled<V>.plusSpecialOrNull(field: ScriptField): Compiled<V>? =
	when (field.name) {
		doName -> plusDo(field.rhs)
		getName -> plusGet(field.rhs)
		makeName -> plusMake(field.rhs)
		else -> null
	}

fun <V> Compiled<V>.plusDo(script: Script): Compiled<V> =
	set(typedTerm.do_(context.plus(binding(given(typedTerm.t))).typedTerm(script)))

fun <V> Compiled<V>.plusGet(script: Script): Compiled<V> =
	script.onlyNameOrNull.notNullOrError("syntax get").let { name ->
		set(
			if (typedTerm.t.isEmpty) context.scope.getOrNull<V>(name).notNullOrError("term get")
			else typedTerm.getOrNull(name).notNullOrError("term get"))
	}

fun <V> Compiled<V>.plusMake(script: Script): Compiled<V> =
	script.onlyNameOrNull.notNullOrError("syntax make").let { name ->
		set(typedTerm.make(name))
	}

fun <V> Compiled<V>.plus(typedLine: TypedLine<V>): Compiled<V> =
	Compiled(context, context.resolve(typedTerm.plus(typedLine)))