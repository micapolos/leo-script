package leo.term.compiler.scheme

import leo.Literal
import leo.isName
import leo.lineTo
import leo.natives.lessName
import leo.natives.thanName
import leo.numberTypeLine
import leo.term.compiler.Environment
import leo.term.compiler.equalsTypeLine
import leo.term.fn
import leo.term.get
import leo.term.head
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.textTypeLine
import leo.type
import scheme.Scheme
import scheme.scheme

val schemeEnvironment: Environment<Scheme>
  get() =
    Environment(
      { literal -> literal.scheme.nativeTerm },
      { typedTerm ->
        when (typedTerm.t) {
          type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) (+ x y)))".scheme.nativeTerm.invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) (- x y)))".scheme.nativeTerm.invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) (* x y)))".scheme.nativeTerm.invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "equals" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) ${"(= x y)".scheme.boolean.string}))".scheme.nativeTerm
                  .invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)).invoke(typedTerm.v),
              type(equalsTypeLine)
            )
          type(numberTypeLine, isName lineTo type(lessName lineTo type(thanName lineTo type(numberTypeLine)))) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) ${"(< x y)".scheme.boolean.string}))".scheme.nativeTerm
                  .invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)).invoke(typedTerm.v),
              type(equalsTypeLine)
            )
          type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
            typed(
              fn(
                "(lambda (x) (lambda (y) (string-append x y)))".scheme.nativeTerm.invoke(get<Scheme>(0).tail)
                  .invoke(get<Scheme>(0).head)
              ).invoke(typedTerm.v),
              type(textTypeLine)
            )
          else -> null
        }
      },
      { it.scriptLine }
    )

val Literal.scheme: Scheme get() = toString().scheme
