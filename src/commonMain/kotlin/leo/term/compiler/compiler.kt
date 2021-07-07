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
import leo.compiledName
import leo.doingName
import leo.functionLineTo
import leo.giveName
import leo.isEmpty
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.matchInfix
import leo.performName
import leo.quoteName
import leo.script
import leo.scriptLine
import leo.term.fn
import leo.term.script
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.give
import leo.term.typed.invoke
import leo.term.typed.lineTo
import leo.term.typed.plus
import leo.term.typed.staticTypedLine
import leo.term.typed.staticTypedTerm
import leo.term.typed.typed
import leo.term.typed.typedTerm

data class Compiler<V>(
	val module: Module<V>,
	val typedTerm: TypedTerm<V>)

val <V> Module<V>.compiler: Compiler<V> get() = Compiler(this, typedTerm())

fun <V> Compiler<V>.set(typedTerm: TypedTerm<V>): Compiler<V> =
	copy(typedTerm = typedTerm)

fun <V> Compiler<V>.set(module: Module<V>): Compiler<V> =
	copy(module = module)

val <V> Compiler<V>.context get() = module.context
val <V> Compiler<V>.environment get() = context.environment

fun <V> Compiler<V>.plus(script: Script): Compiler<V> =
	fold(script.lineSeq.reverse) { plus(it) }

fun <V> Compiler<V>.plus(scriptLine: ScriptLine): Compiler<V> =
	when (scriptLine) {
		is FieldScriptLine -> plus(scriptLine.field)
		is LiteralScriptLine -> plus(scriptLine.literal)
	}

fun <V> Compiler<V>.plus(literal: Literal): Compiler<V> =
	plus(environment.typedLine(literal))

fun <V> Compiler<V>.plus(field: ScriptField): Compiler<V> =
	null
		?: plusSpecialOrNull(field)
		?: plusNamed(field)

fun <V> Compiler<V>.plusNamed(field: ScriptField): Compiler<V> =
	if (field.rhs.isEmpty) set(typedTerm()).plus(field.name lineTo typedTerm)
	else plus(field.name lineTo context.typedTerm(field.rhs))

fun <V> Compiler<V>.plusSpecialOrNull(field: ScriptField): Compiler<V>? =
	when (field.name) {
		actionName -> plusAction(field.rhs)
		compiledName -> plusCompiled(field.rhs)
		giveName -> plusGive(field.rhs)
		performName -> plusPerform(field.rhs)
		letName -> plusLet(field.rhs)
		quoteName -> plusQuote(field.rhs)
		else -> null
	}

fun <V> Compiler<V>.plusAction(script: Script): Compiler<V> =
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

fun <V> Compiler<V>.plusCompiled(script: Script): Compiler<V> =
	environment.typedTerm(script).let {
		plus(
			environment.staticTypedLine(
				"compiled" lineTo script(
					"term" lineTo it.v.script,
					it.t.scriptLine)))
	}

fun <V> Compiler<V>.plusGive(script: Script): Compiler<V> =
	set(typedTerm.give(context.plus(binding(given(typedTerm.t))).typedTerm(script)))

fun <V> Compiler<V>.plusPerform(script: Script): Compiler<V> =
	set(context.typedTerm(script).invoke(typedTerm))

fun <V> Compiler<V>.plusLet(script: Script): Compiler<V> =
	if (typedTerm != typedTerm<V>()) error("let after term")
	else set(module.plusLet(script))

fun <V> Compiler<V>.plusQuote(script: Script): Compiler<V> =
	if (!typedTerm.t.isEmpty) error("$typedTerm not empty")
	else set(environment.staticTypedTerm(script))

fun <V> Compiler<V>.plus(typedLine: TypedLine<V>): Compiler<V> =
	set(context.resolve(typedTerm.plus(typedLine)))

val <V> Compiler<V>.compiledTypedTerm: TypedTerm<V> get() =
	typed(module.seal(typedTerm.v), typedTerm.t)