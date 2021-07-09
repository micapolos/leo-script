package leo.term.compiler.js

import leo.isName
import leo.isTypeLine
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

val jsEnvironment: Environment<Js>
  get() =
    Environment(
      { literal -> literal.js.nativeTerm },
      { typedTerm ->
        when (typedTerm.t) {
          type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x=>y=>x+y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x=>y=>x-y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x=>y=>x*y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(numberTypeLine))
          type(numberTypeLine, "equals" lineTo type(numberTypeLine)) ->
            typed(
              fn("(x=>y=>${"x==y".js.boolean.string})".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(isTypeLine))
          type(numberTypeLine, isName lineTo type(lessName lineTo type(thanName lineTo type(numberTypeLine)))) ->
            typed(
              fn("(x=>y=>${"x<y".js.boolean.string})".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(equalsTypeLine))
          type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
            typed(
              fn("(x=>y=>x+y)".js.nativeTerm.invoke(get<Js>(0).tail).invoke(get<Js>(0).head)).invoke(typedTerm.v),
              type(textTypeLine))
          else -> null
        }
      },
      { it.scriptLine }
    )
