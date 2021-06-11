package leo.indexed.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.TypeLine
import leo.TypeStructure
import leo.base.notNullOrError
import leo.beName
import leo.bind
import leo.castName
import leo.doName
import leo.doingLineTo
import leo.foldStateful
import leo.indexed.expression
import leo.indexed.function
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.onlyTypedOrNull
import leo.indexed.typed.plus
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.indexed.typed.typedTo
import leo.isEmpty
import leo.letName
import leo.lineStack
import leo.map
import leo.matchInfix
import leo.onlyLineOrNull
import leo.reverse
import leo.seq
import leo.size
import leo.stateful
import leo.switchName
import leo.theName
import leo.type.compiler.type

typealias Compilation<T, V> = Stateful<Context<T>, V>
fun <T, V> V.compilation(): Compilation<T, V> = stateful()

fun <T> Context<T>.tupleCompilation(script: Script): Compilation<T, TypedTuple<T>> =
	compiler
		.compilation<T, Compiler<T>>()
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.bodyTuple }

fun <T> Context<T>.typedCompilation(scriptLine: ScriptLine): Compilation<T, Typed<T>> =
	when (scriptLine) {
		is FieldScriptLine -> typedCompilation(scriptLine.field)
		is LiteralScriptLine -> typedCompilation(scriptLine.literal)
	}

fun <T> Context<T>.typedCompilation(scriptField: ScriptField): Compilation<T, Typed<T>> =
	tupleCompilation(scriptField.rhs).map { scriptField.name typedTo it }

fun <T> typedCompilation(literal: Literal): Compilation<T, Typed<T>> =
	typed<T>(literal).compilation()

fun <T> Context<T>.typedCompilation(script: Script): Compilation<T, Typed<T>> =
	tupleCompilation(script).map { it.onlyTypedOrNull.notNullOrError("$it.onlyTypedOrNull") }

fun <T> Context<T>.typeStructureCompilation(script: Script): Compilation<T, TypeStructure> =
	script.type.compileStructure.compilation()

fun <T> Context<T>.typeLineCompilation(script: Script): Compilation<T, TypeLine> =
	typeStructureCompilation(script).map { it.onlyLineOrNull.notNullOrError("$this not line") }

fun <T> Compiler<T>.plusCompilation(script: Script): Compilation<T, Compiler<T>> =
	compilation<T, Compiler<T>>().foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }

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
		beName -> plusBeCompilation(scriptField.rhs)
		castName -> plusCastCompilation(scriptField.rhs)
		doName -> plusDoCompilation(scriptField.rhs)
		letName -> plusLetCompilation(scriptField.rhs)
		switchName -> plusSwitchCompilation(scriptField.rhs)
		theName -> plusTheCompilation(scriptField.rhs)
		else -> null
	}

fun <T> Compiler<T>.plusBeCompilation(script: Script): Compilation<T, Compiler<T>> =
	context.typedCompilation(script).map { set(tuple(it)) }

fun <T> Compiler<T>.plusCastCompilation(script: Script): Compilation<T, Compiler<T>> =
	bodyTuple.compileTyped.let { typed ->
		context.typeLineCompilation(script).map { typeLine ->
			set(tuple(typed.compileCast(typeLine)))
		}
	}

fun <T> Compiler<T>.plusDoCompilation(script: Script): Compilation<T, Compiler<T>>? =
	context.plus(bodyTuple).typedCompilation(script).map { typed ->
		set(
			tuple(
				typed(
					expression(
						invoke(
							expression(function(bodyTuple.typedStack.size, typed.expression)),
							bodyTuple.expressionTuple)),
					typed.typeLine)))
	}

fun <T> Compiler<T>.plusLetCompilation(script: Script): Compilation<T, Compiler<T>> =
	script
		.matchInfix { lhs, name, rhs ->
			when (name) {
				beName -> plusLetBeCompilation(lhs, rhs)
				doName -> plusLetDoCompilation(lhs, rhs)
				else -> null
			}
		}.notNullOrError("$script let error")

fun <T> Compiler<T>.plusSwitchCompilation(script: Script): Compilation<T, Compiler<T>> =
	TODO()

fun <T> Compiler<T>.plusTheCompilation(script: Script): Compilation<T, Compiler<T>> =
	context.typedCompilation(script.compileOnlyLine).map { plus(it) }

fun <T> Compiler<T>.plusLetBeCompilation(lhs: Script, rhs: Script): Compilation<T, Compiler<T>> =
	context.typeStructureCompilation(lhs).bind { typeStructure ->
		context.typedCompilation(rhs).map { typed ->
			set(
				context
					.plus(definition(typeStructure, constantBinding(typed.typeLine)))
					.plusParam(typed))
		}
	}

fun <T> Compiler<T>.plusLetDoCompilation(lhs: Script, rhs: Script): Compilation<T, Compiler<T>> =
	context.typeStructureCompilation(lhs).bind { typeStructure ->
		context.plus(typeStructure).typedCompilation(rhs).map { bodyTyped ->
			context
				.plus(definition(typeStructure, functionBinding(bodyTyped.typeLine)))
				.plusParam(
					typed(
						expression(function(typeStructure.lineStack.size, bodyTyped.expression)),
						typeStructure.doingLineTo(bodyTyped.typeLine)))
				.compiler
		}
	}

fun <T> Compiler<T>.plusDynamicCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun <T> Compiler<T>.plusCompilation(name: String): Compilation<T, Compiler<T>> =
	context.resolveCompilation(tuple(name typedTo bodyTuple)).map { set(it) }

fun <T> Compiler<T>.plusFieldCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	context.tupleCompilation(scriptField.rhs).bind { tuple ->
		plusResolveCompilation(scriptField.name.typedTo(tuple))
	}

fun <T> Compiler<T>.plusResolveCompilation(typed: Typed<T>): Compilation<T, Compiler<T>> =
	context.resolveCompilation(bodyTuple.plus(typed)).map { set(it) }

fun <T> Context<T>.resolveCompilation(tuple: TypedTuple<T>): Compilation<T, TypedTuple<T>> =
	null
		?: resolveCompilationOrNull(tuple)
		?: tuple.resolve.compilation()

fun <T> Context<T>.resolveCompilationOrNull(tuple: TypedTuple<T>): Compilation<T, TypedTuple<T>>? =
	typedOrNull(tuple)?.let { tuple(it) }?.compilation()
