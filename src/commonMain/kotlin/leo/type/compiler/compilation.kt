package leo.type.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.Type
import leo.base.reverse
import leo.expression.compiler.resolve
import leo.foldStateful
import leo.isEmpty
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.map
import leo.stateful
import leo.type

typealias TypeCompilation<T> = Stateful<TypeContext, T>
val <T> T.typeCompilation: TypeCompilation<T> get() = stateful()

fun TypeContext.typeCompilation(script: Script): TypeCompilation<Type> =
	TypeCompiler(this, type()).plusCompilation(script).map { compiler ->
		compiler.type
	}

fun TypeCompiler.plusCompilation(script: Script): TypeCompilation<TypeCompiler> =
	typeCompilation.foldStateful(script.lineSeq.reverse) { plusCompilation(it) }

fun TypeCompiler.plusCompilation(scriptLine: ScriptLine): TypeCompilation<TypeCompiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun TypeCompiler.plusCompilation(scriptField: ScriptField): TypeCompilation<TypeCompiler> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.name)
	else plusFieldCompilation(scriptField)

fun TypeCompiler.plusFieldCompilation(scriptField: ScriptField): TypeCompilation<TypeCompiler> =
	null
		?: plusStaticCompilationOrNull(scriptField)
		?: plusDynamicCompilation(scriptField)

fun TypeCompiler.plusStaticCompilationOrNull(scriptField: ScriptField): TypeCompilation<TypeCompiler>? =
	when (scriptField.name) {
		letName -> TODO()
		else -> null
	}

fun TypeCompiler.plusCompilation(name: String): TypeCompilation<TypeCompiler> =
	set(type(name lineTo type)).resolveCompilation

fun TypeCompiler.plusDynamicCompilation(scriptField: ScriptField): TypeCompilation<TypeCompiler> =
	context.typeCompilation(scriptField.rhs).map { rhsType ->
		set(type.compilePlus(scriptField.name lineTo rhsType))
	}

val TypeCompiler.resolveCompilation: TypeCompilation<TypeCompiler> get() =
	null
		?: context.typeOrNull(type)?.let { set(it).typeCompilation }
		?: set(type.resolve).typeCompilation

@Suppress("unused")
fun TypeCompiler.plusCompilation(literal: Literal): TypeCompilation<TypeCompiler> =
	error("$literal is not a type")