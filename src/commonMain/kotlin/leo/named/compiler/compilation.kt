package leo.named.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stack
import leo.Stateful
import leo.Type
import leo.TypeLine
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.base.notNullOrError
import leo.beName
import leo.bind
import leo.bindName
import leo.debugName
import leo.doName
import leo.doingName
import leo.flat
import leo.fold
import leo.foldStateful
import leo.giveName
import leo.isEmpty
import leo.letName
import leo.lineStack
import leo.lineTo
import leo.map
import leo.matchInfix
import leo.named.expression.binding
import leo.named.expression.caseTo
import leo.named.expression.function
import leo.named.typed.TypedCase
import leo.named.typed.TypedExpression
import leo.named.typed.TypedField
import leo.named.typed.TypedLine
import leo.named.typed.do_
import leo.named.typed.doingTypedLine
import leo.named.typed.fieldTo
import leo.named.typed.invoke
import leo.named.typed.line
import leo.named.typed.lineTo
import leo.named.typed.name
import leo.named.typed.plus
import leo.named.typed.reflectTypedExpression
import leo.named.typed.rhs
import leo.named.typed.switch
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.named.typed.typedLine
import leo.named.typed.with
import leo.normalizeRecursion
import leo.ofName
import leo.quoteName
import leo.recursiveName
import leo.reverse
import leo.script
import leo.seq
import leo.stateful
import leo.switchName
import leo.takeName
import leo.theName
import leo.toName
import leo.type
import leo.typeName
import leo.withName
import leo.zip

typealias Compilation<V> = Stateful<Environment, V>
val <V> V.compilation: Compilation<V> get() = stateful()

fun Dictionary.typedExpressionCompilation(script: Script): Compilation<TypedExpression> =
	context
		.compiler
		.compilation
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.typedExpression }

fun Dictionary.typedLineCompilation(scriptLine: ScriptLine): Compilation<TypedLine> =
	when (scriptLine) {
		is FieldScriptLine -> typedLineCompilation(scriptLine.field)
		is LiteralScriptLine -> typedLineCompilation(scriptLine.literal)
	}

fun Dictionary.typedLineCompilation(scriptField: ScriptField): Compilation<TypedLine> =
	typedExpressionCompilation(scriptField.rhs).map { scriptField.name lineTo it }

fun typedLineCompilation(literal: Literal): Compilation<TypedLine> =
	typedLine(literal).compilation

fun typeCompilation(script: Script): Compilation<Type> =
	script.type.normalizeRecursion.compilation

fun Dictionary.typedLineStackCompilation(script: Script): Compilation<Stack<TypedLine>> =
	script.lineStack.map { typedLineCompilation(this) }.flat

fun Compiler.plusCompilation(script: Script): Compilation<Compiler> =
	compilation.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }

fun  Compiler.plusCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun Compiler.plusCompilation(literal: Literal): Compilation<Compiler> =
	plusResolveCompilation(typedLine(literal))

fun Compiler.plusCompilation(scriptField: ScriptField): Compilation<Compiler> =
	null
		?: plusStaticCompilationOrNull(scriptField)
		?: plusDynamicCompilation(scriptField)

fun Compiler.plusStaticCompilationOrNull(scriptField: ScriptField): Compilation<Compiler>? =
	when (scriptField.name) {
		bindName -> plusBindCompilation(scriptField.rhs)
		ofName -> plusOfCompilation(scriptField.rhs)
		debugName -> plusDebugCompilation(scriptField.rhs)
		doName -> plusDoCompilation(scriptField.rhs)
		doingName -> plusDoingCompilation(scriptField.rhs)
		letName -> plusLetCompilation(scriptField.rhs)
		quoteName -> plusQuoteCompilation(scriptField.rhs)
		recursiveName -> plusRecursiveCompilation(scriptField.rhs)
		switchName -> plusSwitchCompilation(scriptField.rhs)
		theName -> plusTheCompilation(scriptField.rhs)
		else -> null
	}

fun Compiler.plusGetCompilationOrNull(typedField: TypedField): Compilation<Compiler>? =
	ifOrNull(bodyTypedExpression.type.isEmpty) {
		typedField.rhs.getOrNull(typedField.name)?.let {
			set(it).compilation
		}
	}

fun Compiler.plusBeCompilation(typedExpression: TypedExpression): Compilation<Compiler> =
	set(typedExpression).compilation

fun Compiler.plusOfCompilation(script: Script): Compilation<Compiler> =
	typeCompilation(script).map { type ->
		set(bodyTypedExpression.of(type))
	}

fun Compiler.plusDebugCompilation(script: Script): Compilation<Compiler> =
	if (!script.isEmpty) error("debug")
	else plusDebugCompilation

val Compiler.plusDebugCompilation: Compilation<Compiler> get() =
	set(script("debug" lineTo script(scriptLine)).reflectTypedExpression).compilation

fun Compiler.plusDoCompilation(script: Script): Compilation<Compiler>? =
	context
		.dictionary
		.plusNames(bodyTypedExpression.type)
		.typedExpressionCompilation(script)
		.map { set(bodyTypedExpression.do_(it)) }

fun Compiler.plusDoingCompilation(script: Script): Compilation<Compiler> =
	script.matchInfix(toName) { lhs, rhs ->
		typeCompilation(lhs).bind { type ->
			context.dictionary.plusNames(type).typedExpressionCompilation(rhs).map { body ->
				plus(type.doingTypedLine(body))
			}
		}
	}.notNullOrError("$script is not function body")

fun Compiler.plusGiveCompilation(typedExpression: TypedExpression): Compilation<Compiler> =
	set(bodyTypedExpression.invoke(typedExpression)).compilation

fun Compiler.plusLetCompilation(script: Script): Compilation<Compiler> =
	script
		.matchInfix { lhs, name, rhs ->
			when (name) {
				beName -> plusLetBeCompilation(lhs, rhs)
				doName -> plusLetDoCompilation(lhs, rhs)
				else -> null
			}
		}.notNullOrError("$script let error")

fun Compiler.plusQuoteCompilation(script: Script): Compilation<Compiler> =
	set(bodyTypedExpression.with(script.reflectTypedExpression)).compilation

fun Compiler.plusRecursiveCompilation(script: Script): Compilation<Compiler> =
	TODO()

fun Compiler.plusRecursiveCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	TODO()

fun Compiler.plusSwitchCompilation(script: Script): Compilation<Compiler> =
	context.dictionary.switchCompilation(bodyTypedExpression, script).map { set(it) }

fun Dictionary.switchCompilation(typedExpression: TypedExpression, script: Script): Compilation<TypedExpression> =
	typedExpression.choice.let { typedChoice ->
		zip(typedChoice.typeChoice.lineStack, script.lineStack)
			.map { context
				.dictionary
				.typedCaseCompilation(first!!, second!!.compileField)
			}
			.flat
			.map { typedExpression.switch(it) }
	}

fun Dictionary.typedCaseCompilation(caseLine: TypeLine, scriptField: ScriptField): Compilation<TypedCase> =
	plusName(caseLine).typedExpressionCompilation(scriptField.rhs).map { typedExpression ->
		typed(scriptField.name caseTo typedExpression.expression, typedExpression.type)
	}

fun Compiler.plusTakeCompilation(typedExpression: TypedExpression): Compilation<Compiler> =
	set(typedExpression.invoke(bodyTypedExpression)).compilation

fun Compiler.plusTheCompilation(script: Script): Compilation<Compiler> =
	context.dictionary.typedLineStackCompilation(script).map { typedLineStack ->
		fold(typedLineStack.reverse) { plus(it) }
	}

fun Compiler.plusTypeCompilationOrNull(typed: TypedExpression): Compilation<Compiler>? =
	notNullIf(bodyTypedExpression.type.isEmpty) {
		set(typed.type.typedExpression).compilation
	}

fun Compiler.plusWithCompilation(typed: TypedExpression): Compilation<Compiler> =
	set(bodyTypedExpression.with(typed)).compilation

fun Compiler.plusLetBeCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	typeCompilation(lhs).bind { type ->
		context.dictionary.typedExpressionCompilation(rhs).map { typed ->
			set(
				context
					.plus(definition(type, constantBinding(typed.type)))
					.scopePlus(binding(type, typed.expression)))
		}
	}

fun Compiler.plusLetDoCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	typeCompilation(lhs).bind { type ->
		context.dictionary.plusNames(type).typedExpressionCompilation(rhs).map { bodyTyped ->
			context
				.plus(definition(type, functionBinding(bodyTyped.type)))
				.scopePlus(binding(type, function(bodyTyped.expression)))
				.compiler
		}
	}

fun Compiler.plusDynamicCompilation(scriptField: ScriptField): Compilation<Compiler> =
	if (scriptField.rhs.isEmpty) set(typedExpression()).plusResolveCompilation(scriptField.name fieldTo bodyTypedExpression)
	else plusFieldCompilation(scriptField)

fun Compiler.plusFieldCompilation(scriptField: ScriptField): Compilation<Compiler> =
	context.dictionary.typedExpressionCompilation(scriptField.rhs).bind { typedExpression ->
		plusResolveCompilation(scriptField.name fieldTo typedExpression)
	}

fun Compiler.plusResolveCompilation(typed: TypedField): Compilation<Compiler> =
	null
		?: plusGetCompilationOrNull(typed)
		?: plusResolveStaticCompilationOrNull(typed)
		?: context.resolveCompilation(bodyTypedExpression.plus(typed.line)).map { set(it) }

fun Compiler.plusResolveStaticCompilationOrNull(typedField: TypedField): Compilation<Compiler>? =
	when (typedField.name) {
		beName -> plusBeCompilation(typedField.rhs)
		giveName -> plusGiveCompilation(typedField.rhs)
		takeName -> plusTakeCompilation(typedField.rhs)
		typeName -> plusTypeCompilationOrNull(typedField.rhs)
		withName -> plusWithCompilation(typedField.rhs)
		else -> null
	}

fun Compiler.plusBindCompilation(script: Script): Compilation<Compiler> =
	context.dictionary.typedLineStackCompilation(script).map { set(context.bind(it)) }

fun Compiler.plusResolveCompilation(typed: TypedLine): Compilation<Compiler> =
	context.resolveCompilation(bodyTypedExpression.plus(typed)).map { set(it) }

fun Context.resolveCompilation(typedExpression: TypedExpression): Compilation<TypedExpression> =
	null
		?: resolveCompilationOrNull(typedExpression)
		?: typedExpression.resolve.compilation

fun Context.resolveCompilationOrNull(typedExpression: TypedExpression): Compilation<TypedExpression>? =
	resolveOrNull(typedExpression)?.compilation
