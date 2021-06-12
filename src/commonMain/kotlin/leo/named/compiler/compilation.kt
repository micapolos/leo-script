package leo.named.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.TypeLine
import leo.TypeStructure
import leo.base.ifOrNull
import leo.base.notNullOrError
import leo.beName
import leo.bind
import leo.castName
import leo.doName
import leo.doingLineTo
import leo.foldStateful
import leo.indexed.compiler.compileOnlyExpression
import leo.indexed.compiler.compileOnlyLine
import leo.indexed.compiler.compileStructure
import leo.isEmpty
import leo.letName
import leo.lineStack
import leo.map
import leo.matchInfix
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.expressionTo
import leo.named.typed.plus
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.named.typed.typedStructure
import leo.onlyLineOrNull
import leo.reverse
import leo.seq
import leo.stateful
import leo.switchName
import leo.theName
import leo.type.compiler.type

typealias Compilation<T, V> = Stateful<Context<T>, V>
fun <T, V> V.compilation(): Compilation<T, V> = stateful()

fun <T> Context<T>.typedStructureCompilation(script: Script): Compilation<T, TypedStructure<T>> =
	compiler
		.compilation<T, Compiler<T>>()
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.bodyTypedStructure }

fun <T> Context<T>.typedExpressionCompilation(scriptLine: ScriptLine): Compilation<T, TypedExpression<T>> =
	when (scriptLine) {
		is FieldScriptLine -> typedExpressionCompilation(scriptLine.field)
		is LiteralScriptLine -> typedExpressionCompilation(scriptLine.literal)
	}

fun <T> Context<T>.typedExpressionCompilation(scriptField: ScriptField): Compilation<T, TypedExpression<T>> =
	typedStructureCompilation(scriptField.rhs).map { scriptField.name expressionTo it }

fun <T> typedExpressionCompilation(literal: Literal): Compilation<T, TypedExpression<T>> =
	typedExpression<T>(literal).compilation()

fun <T> Context<T>.typedExpressionCompilation(script: Script): Compilation<T, TypedExpression<T>> =
	typedStructureCompilation(script).map { it.compileOnlyExpression }

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
	plusResolveCompilation(typedExpression(literal))

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
		else -> plusGetCompilationOrNull(scriptField)
	}

fun <T> Compiler<T>.plusGetCompilationOrNull(scriptField: ScriptField): Compilation<T, Compiler<T>>? =
	ifOrNull(scriptField.rhs.isEmpty) {
		bodyTypedStructure.getOrNull(scriptField.name)?.let {
			set(it).compilation()
		}
	}

fun <T> Compiler<T>.plusBeCompilation(script: Script): Compilation<T, Compiler<T>> =
	context.typedExpressionCompilation(script).map { set(typedStructure(it)) }

fun <T> Compiler<T>.plusCastCompilation(script: Script): Compilation<T, Compiler<T>> =
	bodyTypedStructure.typeStructure.compileOnlyExpression.let { typedExpression ->
		context.typeLineCompilation(script).map { typeLine ->
			TODO()
			//set(typedStructure(typedExpression.compileCast(typeLine)))
		}
	}

fun <T> Compiler<T>.plusDoCompilation(script: Script): Compilation<T, Compiler<T>>? =
	context
		.plus(bodyTypedStructure.typeStructure)
		.typedExpressionCompilation(script)
		.map { typed ->
			set(
				typedStructure(
					typed(
						expression(
							invoke(
								expression(function(bodyTypedStructure.typeStructure, typed.expression)),
								bodyTypedStructure.structure)
						),
						typed.typeLine)
				)
			)
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
	context.typedExpressionCompilation(script.compileOnlyLine).map { plus(it) }

fun <T> Compiler<T>.plusLetBeCompilation(lhs: Script, rhs: Script): Compilation<T, Compiler<T>> =
	context.typeStructureCompilation(lhs).bind { typeStructure ->
		context.typedExpressionCompilation(rhs).map { typed ->
			set(
				context
					.plus(definition(typeStructure, constantBinding(typed.typeLine)))
					.plusParam(typed))
		}
	}

fun <T> Compiler<T>.plusLetDoCompilation(lhs: Script, rhs: Script): Compilation<T, Compiler<T>> =
	context.typeStructureCompilation(lhs).bind { typeStructure ->
		context.plus(typeStructure).typedExpressionCompilation(rhs).map { bodyTyped ->
			context
				.plus(definition(typeStructure, functionBinding(bodyTyped.typeLine)))
				.plusParam(
					typed(
						expression(function(typeStructure, bodyTyped.expression)),
						typeStructure.doingLineTo(bodyTyped.typeLine))
				)
				.compiler
		}
	}

fun <T> Compiler<T>.plusDynamicCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun <T> Compiler<T>.plusCompilation(name: String): Compilation<T, Compiler<T>> =
	context.resolveCompilation(typedStructure(name expressionTo bodyTypedStructure)).map { set(it) }

fun <T> Compiler<T>.plusFieldCompilation(scriptField: ScriptField): Compilation<T, Compiler<T>> =
	context.typedStructureCompilation(scriptField.rhs).bind { tuple ->
		plusResolveCompilation(scriptField.name expressionTo tuple)
	}

fun <T> Compiler<T>.plusResolveCompilation(typed: TypedExpression<T>): Compilation<T, Compiler<T>> =
	context.resolveCompilation(bodyTypedStructure.plus(typed)).map { set(it) }

fun <T> Context<T>.resolveCompilation(tuple: TypedStructure<T>): Compilation<T, TypedStructure<T>> =
	null
		?: resolveCompilationOrNull(tuple)
		?: tuple.resolve.compilation()

fun <T> Context<T>.resolveCompilationOrNull(tuple: TypedStructure<T>): Compilation<T, TypedStructure<T>>? =
	resolveOrNull(tuple)?.let { typedStructure(it) }?.compilation()
