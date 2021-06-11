package leo.indexed.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.base.notNullOrError
import leo.bind
import leo.foldStateful
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.onlyTypedOrNull
import leo.indexed.typed.plus
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.indexed.typed.typedTo
import leo.isEmpty
import leo.lineStack
import leo.map
import leo.reverse
import leo.seq
import leo.stateful

typealias Compilation<T, V> = Stateful<Context<T>, V>
fun <T, V> V.compilation(): Compilation<T, V> = stateful()

fun <T> Context<T>.vectorCompilation(script: Script): Compilation<T, TypedTuple<T>> =
	compiler
		.compilation<T, Compiler<T>>()
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.tuple }

fun <T> Context<T>.typedCompilation(script: Script): Compilation<T, Typed<T>> =
	vectorCompilation(script).map { it.onlyTypedOrNull.notNullOrError("$it.onlyTypedOrNull") }

fun <T> Compiler<T>.plusCompilation(scriptLine: ScriptLine): Compilation<T, Compiler<T>> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun <T> Compiler<T>.plusCompilation(literal: Literal): Compilation<T, Compiler<T>> =
	plusResolveCompilation(typed(literal))

fun <T> Compiler<T>.plusCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	null
		?: plusStaticCompilationOrNull(scriptField)
		?: plusDynamicCompilation(scriptField)

fun <T> Compiler<T>.plusStaticCompilationOrNull(scriptField: ScriptField): Compilation<T, Compiler<T>>? =
	when (scriptField.name) {
		else -> null
	}

fun <T> Compiler<T>.plusDynamicCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun <T> Compiler<T>.plusCompilation(name: String): Compilation<T, Compiler<T>> =
	context.resolveCompilation(tuple(name typedTo tuple)).map { set(it) }

fun <T> Compiler<T>.plusFieldCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	context.vectorCompilation(scriptField.rhs).bind { tuple ->
		plusResolveCompilation(scriptField.name.typedTo(tuple))
	}

fun <T> Compiler<T>.plusResolveCompilation(typed: Typed<T>): Compilation<T, Compiler<T>> =
	context.resolveCompilation(tuple.plus(typed)).map { set(it) }

fun <T> Context<T>.resolveCompilation(tuple: TypedTuple<T>): Compilation<T, TypedTuple<T>> =
	null
		?: resolveCompilationOrNull(tuple)
		?: tuple.resolve.compilation()

fun <T> Context<T>.resolveCompilationOrNull(tuple: TypedTuple<T>): Compilation<T, TypedTuple<T>>? =
	null // TODO()
