package leo.term.compiler

import leo.Type
import leo.TypeLine
import leo.choice
import leo.empty
import leo.equalsName
import leo.isStatic
import leo.lineTo
import leo.noName
import leo.term.compiler.native.Native
import leo.term.decompiler.script
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.term.value
import leo.type
import leo.yesName

val <V> TypedTerm<V>.type: Type
  get() =
    if (!t.isStatic) error("type not static")
    else typed(value<Native>(empty), t).script.type

val equalsTypeLine: TypeLine
  get() =
    equalsName lineTo type(
      choice(
        yesName lineTo type(),
        noName lineTo type()
      )
    )
