package leo.term.compiler

import leo.Type
import leo.TypeChoice
import leo.applyName
import leo.choiceOrNull
import leo.contentName
import leo.lineTo
import leo.plus
import leo.script
import leo.term.Term
import leo.term.compiled.Compiled
import leo.term.compiled.content
import leo.term.compiled.getOrNull
import leo.term.compiled.infix
import leo.term.compiled.invoke
import leo.term.compiled.prefix
import leo.term.typed.Typed

val <V> Compiled<V>.resolvedOrNull: Compiled<V>?
  get() =
    null
      ?: resolveApplyOrNull
      ?: resolveContentOrNull
      ?: resolveGetOrNull

val <V> Compiled<V>.resolveApplyOrNull: Compiled<V>?
  get() =
    infix(applyName) { lhs, rhs -> rhs.invoke(lhs) }

val <V> Compiled<V>.resolveContentOrNull: Compiled<V>?
  get() =
    prefix(contentName) { it.content }

val <V> Compiled<V>.resolveGetOrNull: Compiled<V>?
  get() =
    prefix { name, rhs -> rhs.getOrNull(name) }

val <V> Compiled<V>.switchTypedChoice: Typed<Term<V>, TypeChoice>
  get() =
    content.let { content ->
      content.type.choiceOrNull.let { choice ->
        if (choice == null) compileError(type.script.plus("is" lineTo script("not" lineTo script("choice"))))
        else TODO()//typed(content.v, choice)
      }
    }

fun <V> Compiled<V>.check(type: Type): Compiled<V> =
  also { if (this.type != type) compileError(this.type.script.plus("is" lineTo script("not" lineTo type.script))) }