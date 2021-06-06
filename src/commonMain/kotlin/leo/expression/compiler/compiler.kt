package leo.expression.compiler

import leo.expression.Structure

data class Compiler(
	val context: Context,
	val structure: Structure)

fun Compiler.set(context: Context): Compiler = copy(context = context)
fun Compiler.set(structure: Structure): Compiler = copy(structure = structure)
