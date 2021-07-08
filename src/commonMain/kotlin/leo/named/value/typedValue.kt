package leo.named.value

import leo.ChoiceType
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypeStructure
import leo.first
import leo.map
import leo.name
import leo.named.typed.Typed
import leo.onlyOrNull
import leo.onlyStack
import leo.zip

val Typed<Value, Type>.lineStack: Stack<Typed<ValueLine, TypeLine>>
  get() =
    when (t) {
      is ChoiceType -> Typed(v, t.choice).choiceLineStack
      is StructureType -> Typed(v, t.structure).structureLineStack
    }

val Typed<Value, TypeChoice>.choiceLineStack: Stack<Typed<ValueLine, TypeLine>>
  get() =
    v.lineStack.onlyOrNull!!.let { line ->
      Typed(line, t.lineStack.first { it.name == line.name }!!).onlyStack
    }

val Typed<Value, TypeStructure>.structureLineStack: Stack<Typed<ValueLine, TypeLine>>
  get() =
    zip(v.lineStack, t.lineStack).map { Typed(first!!, second!!) }

val Typed<ValueField, TypeField>.rhs
  get() =
    Typed(v.value, t.rhsType)