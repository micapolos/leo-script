package leo.type.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.TypeStructure
import leo.base.reverse
import leo.foldStateful
import leo.getStateful
import leo.isEmpty
import leo.lineSeq
import leo.lineTo
import leo.map
import leo.type
import leo.typeStructure

data class TypeCompiler(
	val context: TypeContext,
	val structure: TypeStructure)

val contextTypeCompilation: TypeCompilation<TypeContext> get() = getStateful()

fun TypeCompiler.set(typeContext: TypeContext): TypeCompiler =
	copy(context = typeContext)
fun TypeCompiler.set(typeStructure: TypeStructure): TypeCompiler =
	copy(structure = typeStructure)

fun TypeContext.typeCompilation(script: Script): TypeCompilation<TypeStructure> =
	TypeCompiler(this, typeStructure()).plusCompilation(script).map { compiler ->
		compiler.structure
	}

fun TypeCompiler.plusCompilation(script: Script): TypeCompilation<TypeCompiler> =
	typeCompilaton.foldStateful(script.lineSeq.reverse) { plusCompilation(it) }

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
	context.typeCompilation(scriptField.rhs).map { structure ->
		set(structure)
	}

val TypeCompiler.resolveCompilation: TypeCompilation<TypeCompiler> get() =
	TODO()

@Suppress("unused")
fun TypeCompiler.plusCompilation(literal: Literal): TypeCompilation<TypeCompiler> =
	error("$literal is not a type")