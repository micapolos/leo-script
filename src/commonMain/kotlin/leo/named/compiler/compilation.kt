package leo.named.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.Type
import leo.TypeLine
import leo.base.ifOrNull
import leo.base.notNullOrError
import leo.beName
import leo.bind
import leo.castName
import leo.doName
import leo.doingLineTo
import leo.foldStateful
import leo.isEmpty
import leo.letName
import leo.lineStack
import leo.map
import leo.matchInfix
import leo.named.expression.body
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.lineTo
import leo.named.typed.plus
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.onlyLineOrNull
import leo.reverse
import leo.seq
import leo.stateful
import leo.switchName
import leo.theName
import leo.type
import leo.type.compiler.type

typealias Compilation<V> = Stateful<Environment, V>
fun <V> V.compilation(): Compilation<V> = stateful()

fun  Context.typedExpressionCompilation(script: Script): Compilation<TypedExpression> =
	compiler
		.compilation<Compiler>()
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.typedExpression }

fun  Context.typedLineCompilation(scriptLine: ScriptLine): Compilation<TypedLine> =
	when (scriptLine) {
		is FieldScriptLine -> typedLineCompilation(scriptLine.field)
		is LiteralScriptLine -> typedLineCompilation(scriptLine.literal)
	}

fun  Context.typedLineCompilation(scriptField: ScriptField): Compilation<TypedLine> =
	typedExpressionCompilation(scriptField.rhs).map { scriptField.name lineTo it }

fun  typedLineCompilation(literal: Literal): Compilation<TypedLine> =
	typedExpression(literal).compilation()

fun  Context.typedLineCompilation(script: Script): Compilation<TypedLine> =
	typedExpressionCompilation(script).map { it.compileOnlyLine }

@Suppress("unused")
fun  Context.typeCompilation(script: Script): Compilation<Type> =
	script.type.compilation()

fun  Context.typeLineCompilation(script: Script): Compilation<TypeLine> =
	typeCompilation(script).map { it.onlyLineOrNull.notNullOrError("$this not line") }

fun  Compiler.plusCompilation(script: Script): Compilation<Compiler> =
	compilation<Compiler>().foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }

fun  Compiler.plusCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun  Compiler.plusCompilation(literal: Literal): Compilation<Compiler> =
	plusResolveCompilation(typedExpression(literal))

fun  Compiler.plusCompilation(scriptField: ScriptField): Compilation<Compiler> =
	null
		?: plusStaticCompilationOrNull(scriptField)
		?: plusDynamicCompilation(scriptField)

fun  Compiler.plusStaticCompilationOrNull(scriptField: ScriptField): Compilation<Compiler>? =
	when (scriptField.name) {
		beName -> plusBeCompilation(scriptField.rhs)
		castName -> plusCastCompilation(scriptField.rhs)
		doName -> plusDoCompilation(scriptField.rhs)
		letName -> plusLetCompilation(scriptField.rhs)
		switchName -> plusSwitchCompilation(scriptField.rhs)
		theName -> plusTheCompilation(scriptField.rhs)
		else -> plusGetCompilationOrNull(scriptField)
	}

fun  Compiler.plusGetCompilationOrNull(scriptField: ScriptField): Compilation<Compiler>? =
	ifOrNull(scriptField.rhs.isEmpty) {
		bodyTypedExpression.getOrNull(scriptField.name)?.let {
			set(it).compilation()
		}
	}

fun  Compiler.plusBeCompilation(script: Script): Compilation<Compiler> =
	context.typedLineCompilation(script).map { set(typedExpression(it)) }

fun  Compiler.plusCastCompilation(script: Script): Compilation<Compiler> =
	TODO()

fun  Compiler.plusDoCompilation(script: Script): Compilation<Compiler>? =
	context
		.plusNames(bodyTypedExpression.type)
		.typedExpressionCompilation(script)
		.map { typed ->
			set(
					typed(
						expression(
							invoke(
								expression(line(function(bodyTypedExpression.type, body(typed.expression)))),
								bodyTypedExpression.expression)),
						typed.type)
			)
		}

fun  Compiler.plusLetCompilation(script: Script): Compilation<Compiler> =
	script
		.matchInfix { lhs, name, rhs ->
			when (name) {
				beName -> plusLetBeCompilation(lhs, rhs)
				doName -> plusLetDoCompilation(lhs, rhs)
				else -> null
			}
		}.notNullOrError("$script let error")

fun  Compiler.plusSwitchCompilation(script: Script): Compilation<Compiler> =
	TODO()

fun  Compiler.plusTheCompilation(script: Script): Compilation<Compiler> =
	context.typedLineCompilation(script.compileLine).map { plus(it) }

fun  Compiler.plusLetBeCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	context.typeCompilation(lhs).bind { type ->
		context.typedExpressionCompilation(rhs).map { typed ->
			set(
				context
					.plus(definition(type, constantBinding(typed.type)))
					.plusParam(typed))
		}
	}

fun  Compiler.plusLetDoCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	context.typeCompilation(lhs).bind { type ->
		context.plusNames(type).typedExpressionCompilation(rhs).map { bodyTyped ->
			context
				.plus(definition(type, functionBinding(bodyTyped.type)))
				.plusParam(
					typed(
						expression(line(function(type, body(bodyTyped.expression)))),
						type(type.doingLineTo(bodyTyped.type)))
				)
				.compiler
		}
	}

fun  Compiler.plusDynamicCompilation(scriptField: ScriptField): Compilation<Compiler> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun  Compiler.plusCompilation(name: String): Compilation<Compiler> =
	context.resolveCompilation(typedExpression(name lineTo bodyTypedExpression)).map { set(it) }

fun  Compiler.plusFieldCompilation(scriptField: ScriptField): Compilation<Compiler> =
	context.typedExpressionCompilation(scriptField.rhs).bind { tuple ->
		plusResolveCompilation(scriptField.name lineTo tuple)
	}

fun  Compiler.plusResolveCompilation(typed: TypedLine): Compilation<Compiler> =
	context.resolveCompilation(bodyTypedExpression.plus(typed)).map { set(it) }

fun  Context.resolveCompilation(tuple: TypedExpression): Compilation<TypedExpression> =
	null
		?: resolveCompilationOrNull(tuple)
		?: tuple.resolve.compilation()

fun  Context.resolveCompilationOrNull(tuple: TypedExpression): Compilation<TypedExpression>? =
	resolveOrNull(tuple)?.compilation()
