package leo.type.compiler

import leo.TypeStructure
import leo.getStateful

data class TypeCompiler(
	val context: TypeContext,
	val structure: TypeStructure)

val contextTypeCompilation: TypeCompilation<TypeContext> get() = getStateful()

fun TypeCompiler.set(typeContext: TypeContext): TypeCompiler =
	copy(context = typeContext)
fun TypeCompiler.set(typeStructure: TypeStructure): TypeCompiler =
	copy(structure = typeStructure)
