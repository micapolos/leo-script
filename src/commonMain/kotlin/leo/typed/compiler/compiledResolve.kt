package leo.typed.compiler

import leo.Type
import leo.applyName
import leo.contentName
import leo.lineTo
import leo.plus
import leo.script
import leo.typed.compiled.Compiled
import leo.typed.compiled.content
import leo.typed.compiled.getOrNull
import leo.typed.compiled.infix
import leo.typed.compiled.invoke
import leo.typed.compiled.prefix

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

fun <V> Compiled<V>.check(type: Type): Compiled<V> =
  also { if (this.type != type) compileError(this.type.script.plus("is" lineTo script("not" lineTo type.script))) }