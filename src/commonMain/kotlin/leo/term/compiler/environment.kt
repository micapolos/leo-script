package leo.term.compiler

import leo.Literal
import leo.Script
import leo.ScriptLine
import leo.isEmpty
import leo.lineTo
import leo.plus
import leo.script
import leo.term.Term
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.infix
import leo.term.typed.typed
import leo.typeLine
import leo.typeName

data class Environment<V>(
  val literalFn: (Literal) -> Term<V>,
  val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?,
  val scriptLineFn: (V) -> ScriptLine)

fun <V> Environment<V>.typedTerm(script: Script): TypedTerm<V> =
  context.typedTerm(script)

fun <V> Environment<V>.typedLine(literal: Literal): TypedLine<V> =
  typed(literalFn(literal), literal.typeLine)

fun <V> Environment<V>.resolveTypeOrNull(typedTerm: TypedTerm<V>): TypedTerm<V>? =
  typedTerm.infix(typeName) { lhs, rhs ->
    if (!lhs.t.isEmpty) compileError(
      lhs.t.script
        .plus("type" lineTo rhs.t.script)
        .plus("is" lineTo script(
          "not" lineTo script(
            "matching" lineTo script(
              "type" lineTo script(
                "any" lineTo script(
                  "compiled")))))))
    else resolveType(rhs)
  }

fun <V> Script.typedTerm(environment: Environment<V>): TypedTerm<V> =
  environment.typedTerm(this)