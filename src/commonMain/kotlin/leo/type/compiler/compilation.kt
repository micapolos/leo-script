package leo.type.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stateful
import leo.TypeStructure
import leo.base.reverse
import leo.expression.compiler.resolveGetOrNull
import leo.foldStateful
import leo.isEmpty
import leo.lineSeq
import leo.lineTo
import leo.map
import leo.plus
import leo.stateful
import leo.type
import leo.typeStructure

typealias TypeCompilation<T> = Stateful<TypeContext, T>
val <T> T.typeCompilation: TypeCompilation<T> get() = stateful()

fun TypeContext.structureCompilation(script: Script): TypeCompilation<TypeStructure> =
	TypeCompiler(this, typeStructure()).plusCompilation(script).map { compiler ->
		compiler.structure
	}

fun TypeCompiler.plusCompilation(script: Script): TypeCompilation<TypeCompiler> =
	typeCompilation.foldStateful(script.lineSeq.reverse) { plusCompilation(it) }

fun TypeCompiler.plusCompilation(scriptLine: ScriptLine): TypeCompilation<TypeCompiler> =
	when (scriptLine) {
		is FieldScriptLine -> plusCompilation(scriptLine.field)
		is LiteralScriptLine -> plusCompilation(scriptLine.literal)
	}

fun TypeCompiler.plusCompilation(scriptField: ScriptField): TypeCompilation<TypeCompiler> =
	if (scriptField.rhs.isEmpty) plusCompilation(scriptField.string)
	else plusDynamicCompilation(scriptField)

fun TypeCompiler.plusCompilation(name: String): TypeCompilation<TypeCompiler> =
	set(typeStructure(name lineTo type(structure))).resolveCompilation

fun TypeCompiler.plusDynamicCompilation(scriptField: ScriptField): TypeCompilation<TypeCompiler> =
	context.structureCompilation(scriptField.rhs).map { rhsStructure ->
		set(structure.plus(scriptField.string lineTo type(rhsStructure)))
	}

val TypeCompiler.resolveCompilation: TypeCompilation<TypeCompiler> get() =
	null
		?: structure.resolveGetOrNull?.let { set(it).typeCompilation }
		?: typeCompilation

@Suppress("unused")
fun TypeCompiler.plusCompilation(literal: Literal): TypeCompilation<TypeCompiler> =
	error("$literal is not a type")