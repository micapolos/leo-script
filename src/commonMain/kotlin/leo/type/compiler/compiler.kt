package leo.type.compiler

import leo.Type
import leo.getStateful

data class TypeCompiler(
	val context: TypeContext,
	val type: Type)

val contextTypeCompilation: TypeCompilation<TypeContext> get() = getStateful()

fun TypeCompiler.set(typeContext: TypeContext): TypeCompiler =
	copy(context = typeContext)
fun TypeCompiler.set(type: Type): TypeCompiler =
	copy(type = type)
