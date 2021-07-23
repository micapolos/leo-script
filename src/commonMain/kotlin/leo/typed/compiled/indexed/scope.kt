package leo.typed.compiled.indexed

import leo.IndexVariable
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.base.firstOrNull
import leo.base.mapIndexed
import leo.fold
import leo.name
import leo.push
import leo.reverse
import leo.seq
import leo.stack
import leo.structureOrNull
import leo.type
import leo.typed.compiled.TypeVariable
import leo.variable

data class Scope(val typeStack: Stack<Type>)

val Stack<Type>.scope get() = Scope(this)

fun scope(vararg types: Type) = stack(*types).scope

fun Scope.plus(type: Type) = typeStack.push(type).scope

fun Scope.indexVariable(typeVariable: TypeVariable): IndexVariable =
  variable(typeStack.seq.mapIndexed.firstOrNull { value == typeVariable.type }!!.index)

fun Scope.plusNames(type: Type): Scope =
  fold(type.structureOrNull!!.lineStack.reverse) { plusName(it) }

fun Scope.plusName(typeLine: TypeLine): Scope =
  plus(type(typeLine.name))
