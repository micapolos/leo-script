package leo.term.compiler

import leo.TypeChoice
import leo.applyName
import leo.choiceOrNull
import leo.contentName
import leo.lineTo
import leo.script
import leo.term.Term
import leo.term.typed.Typed
import leo.term.typed.TypedTerm
import leo.term.typed.content
import leo.term.typed.getOrNull
import leo.term.typed.infix
import leo.term.typed.invoke
import leo.term.typed.prefix
import leo.term.typed.typed

val <V> TypedTerm<V>.resolvedOrNull: TypedTerm<V>?
  get() =
    null
      ?: resolveApplyOrNull
      ?: resolveContentOrNull
      ?: resolveGetOrNull

val <V> TypedTerm<V>.resolveApplyOrNull: TypedTerm<V>?
  get() =
    infix(applyName) { lhs, rhs -> rhs.invoke(lhs) }

val <V> TypedTerm<V>.resolveContentOrNull: TypedTerm<V>?
  get() =
    prefix(contentName) { it.content }

val <V> TypedTerm<V>.resolveGetOrNull: TypedTerm<V>?
  get() =
    prefix { name, rhs -> rhs.getOrNull(name) }

val <V> TypedTerm<V>.switchTypedChoice: Typed<Term<V>, TypeChoice>
  get() =
    content.let { content ->
      content.t.choiceOrNull.let { choice ->
        if (choice == null) compileError("switch" lineTo script())
        else typed(content.v, choice)
      }
    }
