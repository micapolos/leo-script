package leo.expression.compiler

import leo.Script
import leo.TypeLine
import leo.expression.Structure
import leo.get

data class Context(
	val staticDictionary: Dictionary,
	val dynamicDictionary: Dictionary)

fun context() = Context(dictionary(), dictionary())

data class Binding(val typeLine: TypeLine, val isFunction: Boolean)

fun Context.structure(script: Script): Structure =
	structureCompilation(script).get(this)