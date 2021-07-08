package leo.term.compiler.julia

import leo.Literal
import leo.lineTo
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

val juliaEnvironment: Environment<Julia>
  get() =
    Environment(
      { literal -> literal.julia.nativeTerm },
      { typedTerm ->
        when (typedTerm.t) {
          type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x->y->x+y)".julia.nativeTerm.invoke(get<Julia>(0).tail).invoke(get<Julia>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x->y->x-y)".julia.nativeTerm.invoke(get<Julia>(0).tail).invoke(get<Julia>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x->y->x*y)".julia.nativeTerm.invoke(get<Julia>(0).tail).invoke(get<Julia>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "equals" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x->y->(x==y) ? (f0->f1->f0(x->x)) : (f0->f1->f1(x->x)))".julia.nativeTerm.invoke(get<Julia>(0).tail).invoke(get<Julia>(0).head)).invoke(typedTerm.v),
              type(equalsTypeLine))
          type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
            typed(
              fn("(x->y->x+y)".julia.nativeTerm.invoke(get<Julia>(0).tail).invoke(get<Julia>(0).head)).invoke(typedTerm.v),
              type(textTypeLine))
          else -> null
        }
      }
    )

val Literal.julia: Julia get() = toString().julia
