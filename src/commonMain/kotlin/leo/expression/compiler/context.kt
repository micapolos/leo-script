package leo.expression.compiler

import leo.Script
import leo.expression.Structure
import leo.get

data class Context(
	val staticDictionary: Dictionary,
	val dynamicDictionary: Dictionary)

fun context() = Context(dictionary(), dictionary())

fun Context.structure(script: Script): Structure =
	structureCompilation(script).get(this)

fun Context.bind(structure: Structure): Context =
	TODO()