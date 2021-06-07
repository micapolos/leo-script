package leo.expression.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.base.notNullIf
import leo.base.reverse
import leo.beName
import leo.bind
import leo.bindName
import leo.commentName
import leo.exampleName
import leo.expression.Expression
import leo.expression.Structure
import leo.expression.applyBind
import leo.expression.dsl.expression
import leo.expression.expression
import leo.expression.expressionTo
import leo.expression.isEmpty
import leo.expression.of
import leo.expression.op
import leo.expression.plus
import leo.expression.resolveEqual
import leo.expression.resolveGetOrNull
import leo.expression.resolveMake
import leo.expression.structure
import leo.expression.typeStructure
import leo.expression.variable
import leo.foldStateful
import leo.getStateful
import leo.isEmpty
import leo.isName
import leo.letName
import leo.lineSeq
import leo.map
import leo.noName
import leo.script
import leo.stateful
import leo.typeStructure
import leo.yesName

typealias Compilation<T> = Stateful<Context, T>
val <T> T.compilation: Compilation<T> get() = stateful()

val contextCompilation: Compilation<Context> get() = getStateful()

fun Context.structureCompilation(script: Script): Compilation<Structure> =
	Compiler(this, structure()).plusCompilation(script).map { it.structure }

fun Context.expressionCompilation(script: Script): Compilation<Expression> =
	structureCompilation(script).map { it.expression }

fun Compiler.plusCompilation(script: Script): Compilation<Compiler> =
	compilation.foldStateful(script.lineSeq.reverse) { plusCompilation(it) }

fun Compiler.plusCompilation(scriptLine: ScriptLine): Compilation<Compiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun Compiler.plusCompilation(scriptField: ScriptField): Compilation<Compiler> =
	null
		?: plusStaticCompilationOrNull(scriptField)
		?: plusDynamicCompilation(scriptField)

fun Compiler.plusStaticCompilationOrNull(scriptField: ScriptField): Compilation<Compiler>? =
	when (scriptField.string) {
		beName -> plusBeCompilation(scriptField.rhs)
		bindName -> plusBindCompilation(scriptField.rhs)
		commentName -> compilation
		exampleName -> plusExampleCompilation(scriptField.rhs)
		isName -> plusIsCompilation(scriptField.rhs)
		letName -> plusLetCompilation(scriptField.rhs)
		else -> notNullIf(scriptField.rhs.isEmpty) { plusCompilation(scriptField.string) }
	}

fun Compiler.plusBeCompilation(script: Script): Compilation<Compiler> =
	context.structureCompilation(script).map { set(it) }

fun Compiler.plusBindCompilation(script: Script): Compilation<Compiler> =
	context.bind(structure.typeStructure).expressionCompilation(script).map { expression ->
		set(structure.applyBind(expression))
	}

fun Compiler.plusExampleCompilation(script: Script): Compilation<Compiler> =
	context.structureCompilation(script).map { this }

fun Compiler.plusIsCompilation(script: Script): Compilation<Compiler> =
	null
		?: plusIsBooleanCompilationOrNull(script)
		?: plusIsExpressionCompilationOrNull(script)

fun Compiler.plusIsBooleanCompilationOrNull(script: Script): Compilation<Compiler>? =
	when (script) {
		script(yesName) -> set(true.structure).compilation
		script(noName) -> set(false.structure).compilation
		else -> null
	}

fun Compiler.plusIsExpressionCompilationOrNull(script: Script): Compilation<Compiler> =
	structure.expression.let { lhsExpression ->
		context.expressionCompilation(script).map { rhsExpression ->
			set(lhsExpression.resolveEqual(rhsExpression).structure)
		}
	}

fun Compiler.plusLetCompilation(script: Script): Compilation<Compiler> =
	TODO()

fun Compiler.plusDynamicCompilation(scriptField: ScriptField): Compilation<Compiler> =
	context.structureCompilation(scriptField.rhs).bind { rhsStructure ->
		set(structure.plus(scriptField.string expressionTo rhsStructure)).resolveCompilation
	}

fun Compiler.plusCompilation(literal: Literal): Compilation<Compiler> =
	set(structure.plus(literal.expression)).resolveCompilation

fun Compiler.plusCompilation(name: String): Compilation<Compiler> =
	if (structure.isEmpty)
		null
			?: context.structureCompilationOrNull(name)?.map { set(it) }
			?: set(name.structure).compilation
	else
		null
			?: structure.resolveGetOrNull(name)?.let { set(it).compilation }
			?: set(structure.resolveMake(name)).resolveCompilation

val Compiler.resolveCompilation: Compilation<Compiler> get() =
	null
		?: context.structureCompilationOrNull(structure)?.map { set(it) }
		?: compilation

fun Context.structureCompilationOrNull(structure: Structure): Compilation<Structure>? =
	null
		?: dynamicDictionary.dynamicStructureCompilationOrNull(structure)
		?: staticDictionary.staticStructureCompilationOrNull(structure)

fun Context.structureCompilationOrNull(name: String): Compilation<Structure>? =
	null
		?: dynamicDictionary.dynamicStructureCompilationOrNull(name)
		?: staticDictionary.staticStructureCompilationOrNull(name)

fun Dictionary.dynamicStructureCompilationOrNull(name: String): Compilation<Structure>? =
	bindingOrNull(name.typeStructure)
		?.let { binding ->
			if (binding.isFunction) TODO()
			else name.variable.op.of(binding.typeLine).structure.compilation
		}

fun Dictionary.staticStructureCompilationOrNull(name: String): Compilation<Structure>? =
	bindingOrNull(name.typeStructure)
		?.let { binding -> TODO() }

fun Dictionary.dynamicStructureCompilationOrNull(structure: Structure): Compilation<Structure>? =
	null // TODO()

fun Dictionary.staticStructureCompilationOrNull(structure: Structure): Compilation<Structure>? =
	null // TODO()
