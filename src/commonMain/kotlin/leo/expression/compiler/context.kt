package leo.expression.compiler

import leo.Script
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.expression.Structure
import leo.get
import leo.reverse
import leo.seq

data class Context(
	val staticDictionary: Dictionary,
	val dynamicDictionary: Dictionary)

fun context() = Context(dictionary(), dictionary())

fun Context.structure(script: Script): Structure =
	structureCompilation(script).get(this)

fun Context.bind(typeStructure: TypeStructure): Context =
	fold(typeStructure.lineStack.reverse.seq) { bind(it) }

fun Context.bind(typeLine: TypeLine): Context =
	copy(dynamicDictionary = dynamicDictionary.dynamicPlusConstantBinding(typeLine))
