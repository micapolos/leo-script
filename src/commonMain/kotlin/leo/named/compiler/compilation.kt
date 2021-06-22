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
import leo.defineName
import leo.doName
import leo.doingName
import leo.equalName
import leo.flat
import leo.fold
import leo.foldStateful
import leo.functionName
import leo.functionTo
import leo.giveName
import leo.givingName
import leo.isEmpty
import leo.isName
import leo.letName
import leo.lineStack
import leo.lineTo
import leo.map
import leo.matchEmpty
import leo.matchInfix
import leo.matchPrefix
import leo.named.evaluator.value
import leo.named.expression.caseTo
import leo.named.expression.doing
import leo.named.expression.function
import leo.named.typed.TypedCase
import leo.named.typed.TypedExpression
import leo.named.typed.TypedField
import leo.named.typed.TypedFunction
import leo.named.typed.TypedLine
import leo.named.typed.do_
import leo.named.typed.fieldTo
import leo.named.typed.functionTypedLine
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
import leo.named.value.script
import leo.normalizeRecursion
import leo.notName
import leo.ofName
import leo.privateName
import leo.quoteName
import leo.recursiveName
import leo.reverse
import leo.script
import leo.seq
import leo.stateful
import leo.switchName
import leo.takeName
import leo.takingName
import leo.theName
import leo.toName
import leo.type
import leo.typeName
import leo.withName
import leo.zip

typealias Compilation<V> = Stateful<Environment, V>
val <V> V.compilation: Compilation<V> get() = stateful()

fun Context.typedExpressionCompilation(script: Script): Compilation<TypedExpression> =
	module.compiler.compilation
		.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }
		.map { it.typedExpression }

fun Context.typedLineCompilation(scriptLine: ScriptLine): Compilation<TypedLine> =
	when (scriptLine) {
		is FieldScriptLine -> typedLineCompilation(scriptLine.field)
		is LiteralScriptLine -> typedLineCompilation(scriptLine.literal)
	}

fun Context.typedLineCompilation(scriptField: ScriptField): Compilation<TypedLine> =
	typedExpressionCompilation(scriptField.rhs).map { scriptField.name lineTo it }

fun typedLineCompilation(literal: Literal): Compilation<TypedLine> =
	typedLine(literal).compilation

fun typeCompilation(script: Script): Compilation<Type> =
	script.value.script.type.normalizeRecursion.compilation

fun Context.typedLineStackCompilation(script: Script): Compilation<Stack<TypedLine>> =
	script.lineStack.map { typedLineCompilation(this) }.flat

fun Compiler.plusCompilation(script: Script): Compilation<Compiler> =
	compilation.foldStateful(script.lineStack.reverse.seq) { plusCompilation(it) }

fun Compiler.plusCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
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
		beName -> plusBeCompilation(scriptField.rhs)
		bindName -> plusBindCompilation(scriptField.rhs)
		debugName -> plusDebugCompilation(scriptField.rhs)
		defineName -> plusDefineCompilation(scriptField.rhs)
		doName -> plusDoCompilation(scriptField.rhs)
		functionName -> plusFunctionOrNullCompilation(scriptField.rhs)
		giveName -> plusGiveCompilation(scriptField.rhs)
		isName -> plusIsCompilationOrNull(scriptField.rhs)
		letName -> plusLetCompilation(scriptField.rhs)
		ofName -> plusOfCompilation(scriptField.rhs)
		privateName -> plusPrivateCompilation(scriptField.rhs)
		quoteName -> plusQuoteCompilation(scriptField.rhs)
		recursiveName -> plusRecursiveCompilation(scriptField.rhs)
		switchName -> plusSwitchCompilation(scriptField.rhs)
		takeName -> plusTakeCompilation(scriptField.rhs)
		theName -> plusTheCompilation(scriptField.rhs)
		typeName -> plusTypeCompilationOrNull(scriptField.rhs)
		withName -> plusWithCompilation(scriptField.rhs)
		else -> null
	}

fun Compiler.plusGetCompilationOrNull(name: String): Compilation<Compiler>? =
	typedExpression.getOrNull(name)?.let { set(it).compilation }

fun Compiler.plusGetCompilationOrNull(typedField: TypedField): Compilation<Compiler>? =
	ifOrNull(typedExpression.type.isEmpty) {
		typedField.rhs.getOrNull(typedField.name)?.let {
			set(it).compilation
		}
	}

fun Compiler.plusBeCompilation(script: Script): Compilation<Compiler> =
	childContext.typedExpressionCompilation(script).map { be(it) }

fun Compiler.plusBindCompilation(script: Script): Compilation<Compiler> =
	childContext.typedExpressionCompilation(script).map { bind(it) }

fun Compiler.plusOfCompilation(script: Script): Compilation<Compiler> =
	typeCompilation(script).map { of(it) }

fun Compiler.plusDebugCompilation(script: Script): Compilation<Compiler> =
	if (!script.isEmpty) error("debug")
	else plusDebugCompilation

fun Compiler.plusDefineCompilation(script: Script): Compilation<Compiler> =
	compilation.foldStateful(script.lineStack.reverse.seq) { plusDefineCompilation(it) }

fun Compiler.plusDefineCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusDefineCompilation(scriptLine.field)
		is LiteralScriptLine -> plusDefineCompilation(scriptLine.literal)
	}

fun Compiler.plusDefineCompilation(scriptField: ScriptField): Compilation<Compiler> =
	when (scriptField.name) {
		typeName -> plusDefineTypeCompilation(scriptField.rhs)
		functionName -> plusDefineFunctionCompilationOrNull(scriptField.rhs)
		else -> null
	} ?: plusDefineFieldCompilation(scriptField)

fun Compiler.plusDefineCompilation(literal: Literal): Compilation<Compiler> =
	typedLineCompilation(literal).map { bind(it) }

fun Compiler.plusDefineFieldCompilation(scriptField: ScriptField): Compilation<Compiler> =
	childContext.typedExpressionCompilation(scriptField.rhs).map { define(scriptField.name fieldTo it) }

fun Compiler.plusDefineTypeCompilation(script: Script): Compilation<Compiler> =
	typeCompilation(script).map { define(it) }

fun Compiler.plusDefineFunctionCompilationOrNull(script: Script): Compilation<Compiler>? =
	typedFunctionCompilationOrNull(script)?.map { define(it) }

fun Compiler.typedFunctionCompilationOrNull(script: Script): Compilation<TypedFunction>? =
	script.matchInfix(doingName) { lhs, doingScript ->
		typedFunctionDoingCompilationOrNull(lhs, doingScript)
	}

fun Compiler.typedFunctionDoingCompilationOrNull(script: Script, doingScript: Script): Compilation<TypedFunction>? =
	script.matchInfix { lhs, name, rhs ->
		when (name) {
			givingName -> typedFunctionGivingDoingCompilationOrNull(lhs, rhs, doingScript)
			takingName -> typedFunctionTakingDoingCompilationOrNull(lhs, rhs, doingScript)
			else -> null
		}
	}

fun Compiler.typedFunctionGivingDoingCompilationOrNull(
	script: Script,
	givingScript: Script,
	doingScript: Script): Compilation<TypedFunction>? =
	script.matchPrefix(takingName) { takingScript ->
		typedFunctionTakingGivingDoingCompilationOrNull(takingScript, givingScript, doingScript)
	}

fun Compiler.typedFunctionTakingDoingCompilationOrNull(
	script: Script,
	takingScript: Script,
	doingScript: Script): Compilation<TypedFunction>? =
	script.matchEmpty {
		typeCompilation(takingScript).bind { takingType ->
			childContext
				.plus(takingType.doDictionary)
				.typedExpressionCompilation(doingScript)
				.map { body ->
					typed(
						function(takingType, doing(body.expression)),
						takingType functionTo body.type)
				}
		}
	}

fun Compiler.typedFunctionTakingGivingDoingCompilationOrNull(
	takingScript: Script, givingScript: Script, doingScript: Script): Compilation<TypedFunction> =
	typeCompilation(takingScript).bind { takingType ->
		typeCompilation(givingScript).bind { givingType ->
			childContext
				.plus(takingType.doDictionary)
				.typedExpressionCompilation(doingScript)
				.map { body ->
					body.type.check(givingType) {
						typed(
							function(takingType, doing(body.expression)),
							takingType functionTo givingType)
					}
				}
		}
	}

val Compiler.plusDebugCompilation: Compilation<Compiler> get() =
	set(script("debug" lineTo script(scriptLine)).reflectTypedExpression).compilation

fun Compiler.plusDoCompilation(script: Script): Compilation<Compiler> =
	childContext
		.plus(typedExpression.type.doDictionary)
		.typedExpressionCompilation(script)
		.map { set(typedExpression.do_(it)) }

fun Compiler.plusFunctionOrNullCompilation(script: Script): Compilation<Compiler>? =
	script.matchInfix(doingName) { lhs, doingScript ->
		plusFunctionDoingOrNullCompilation(lhs, doingScript)
	}

fun Compiler.plusFunctionDoingOrNullCompilation(script: Script, doingScript: Script): Compilation<Compiler>? =
	script.matchInfix { lhs, name, rhs ->
		when (name) {
			givingName -> plusFunctionGivingDoingOrNullCompilation(lhs, rhs, doingScript)
			takingName -> plusFunctionTakingDoingOrNullCompilation(lhs, rhs, doingScript)
			else -> null
		}
	}

fun Compiler.plusFunctionGivingDoingOrNullCompilation(
		script: Script,
		givingScript: Script,
		doingScript: Script): Compilation<Compiler>? =
	script.matchPrefix(takingName) { takingScript ->
		plusFunctionTakingGivingDoingOrNullCompilation(takingScript, givingScript, doingScript)
	}

fun Compiler.plusFunctionTakingDoingOrNullCompilation(
		script: Script,
		takingScript: Script,
		doingScript: Script): Compilation<Compiler>? =
	script.matchEmpty {
		typeCompilation(takingScript).bind { takingType ->
			childContext
				.plus(takingType.doDictionary)
				.typedExpressionCompilation(doingScript)
				.map { body ->
					plus(takingType.functionTypedLine(body))
			}
		}
	}

fun Compiler.plusFunctionTakingGivingDoingOrNullCompilation(
		takingScript: Script, givingScript: Script, doingScript: Script): Compilation<Compiler> =
	typeCompilation(takingScript).bind { takingType ->
		typeCompilation(givingScript).bind { givingType ->
			childContext
				.plus(takingType.doDictionary)
				.typedExpressionCompilation(doingScript)
				.map { body ->
					body.type.check(givingType) {
						plus(takingType.functionTypedLine(body))
					}
				}
		}
	}

fun Compiler.plusGiveCompilation(script: Script): Compilation<Compiler> =
	childContext.typedExpressionCompilation(script).map { give(it) }

fun Compiler.plusIsCompilationOrNull(script: Script): Compilation<Compiler>? =
	script
		.matchPrefix(notName) { notScript -> plusNegableIsCompilationOrNull(notScript)?.map { it.plusNegate } }
		?: plusNegableIsCompilationOrNull(script)

fun Compiler.plusNegableIsCompilationOrNull(script: Script): Compilation<Compiler>? =
	plusIsEqualToCompilationOrNull(script)

fun Compiler.plusIsEqualToCompilationOrNull(script: Script): Compilation<Compiler>? =
	script.matchPrefix(equalName) { equalScript ->
		equalScript.matchPrefix(toName) { toScript ->
			childContext.typedExpressionCompilation(toScript).map { plusIsEqualTo(it) }
		}
	}

fun Compiler.plusLetCompilation(script: Script): Compilation<Compiler> =
	script
		.matchInfix { lhs, name, rhs ->
			when (name) {
				beName -> plusLetBeCompilation(lhs, rhs)
				doName -> plusLetDoCompilation(lhs, rhs)
				else -> null
			}
		}.notNullOrError("$script let error")

fun Compiler.plusPrivateCompilation(script: Script): Compilation<Compiler> =
	childContext.module.compiler.plusCompilation(script).map { plusPrivate(it) }

fun Compiler.plusQuoteCompilation(script: Script): Compilation<Compiler> =
	set(typedExpression.with(script.reflectTypedExpression)).compilation

fun Compiler.plusRecursiveCompilation(script: Script): Compilation<Compiler> =
	TODO()

fun Compiler.plusRecursiveCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	TODO()

fun Compiler.plusSwitchCompilation(script: Script): Compilation<Compiler> =
	childContext.switchCompilation(typedExpression, script).map { set(it) }

fun Context.switchCompilation(typedExpression: TypedExpression, script: Script): Compilation<TypedExpression> =
	typedExpression.choice.let { typedChoice ->
		zip(typedChoice.typeChoice.lineStack, script.lineStack)
			.map { typedCaseCompilation(first!!, second!!.compileField)
			}
			.flat
			.map { typedExpression.switch(it) }
	}

fun Context.typedCaseCompilation(caseLine: TypeLine, scriptField: ScriptField): Compilation<TypedCase> =
	plus(caseLine.bindDefinition).typedExpressionCompilation(scriptField.rhs).map { typedExpression ->
		typed(scriptField.name caseTo typedExpression.expression, typedExpression.type)
	}

fun Compiler.plusTakeCompilation(script: Script): Compilation<Compiler> =
	childContext.typedExpressionCompilation(script).map { take(it) }

fun Compiler.plusTheCompilation(script: Script): Compilation<Compiler> =
	childContext.typedLineStackCompilation(script).map { typedLineStack ->
		fold(typedLineStack.reverse) { plus(it) }
	}

fun Compiler.plusTypeCompilationOrNull(script: Script): Compilation<Compiler>? =
	notNullIf(script.isEmpty) {
		set(typedExpression.type.typedExpression).compilation
	}

fun Compiler.plusWithCompilation(script: Script): Compilation<Compiler> =
	childContext.typedExpressionCompilation(script).map { with(it) }

fun Compiler.plusLetBeCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	typeCompilation(lhs).bind { type ->
		childContext.typedExpressionCompilation(rhs).map { typedExpression ->
			letBe(type, typedExpression)
		}
	}

fun Compiler.plusLetDoCompilation(lhs: Script, rhs: Script): Compilation<Compiler> =
	typeCompilation(lhs).bind { type ->
		childContext.plus(type.doDictionary).typedExpressionCompilation(rhs).map { typedExpression ->
			letDo(type, typedExpression)
		}
	}

fun Compiler.plusDynamicCompilation(scriptField: ScriptField): Compilation<Compiler> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun Compiler.plusCompilation(name: String): Compilation<Compiler> =
	null
		?:plusGetCompilationOrNull(name)
		?:plusMakeCompilation(name)

fun Compiler.plusMakeCompilation(name: String): Compilation<Compiler> =
	typedExpression.make(name).let {
		set(it).resolveCompilation
	}

fun Compiler.plusFieldCompilation(scriptField: ScriptField): Compilation<Compiler> =
	childContext.typedExpressionCompilation(scriptField.rhs).bind { typedExpression ->
		plusResolveCompilation(scriptField.name fieldTo typedExpression)
	}

fun Compiler.plusResolveCompilation(typed: TypedField): Compilation<Compiler> =
	null
		?: plusGetCompilationOrNull(typed)
		?: childContext.resolveCompilation(typedExpression.plus(typed.line)).map { set(it) }

fun Compiler.plusResolveCompilation(typed: TypedLine): Compilation<Compiler> =
	set(childContext.resolve(typedExpression.plus(typed))).compilation

val Compiler.resolveCompilation: Compilation<Compiler> get() =
	set(childContext.resolve(typedExpression)).compilation

fun Context.resolveCompilation(typedExpression: TypedExpression): Compilation<TypedExpression> =
	resolve(typedExpression).compilation

val Compiler.childContext: Context get() = module.privateContext