package leo.term.compiler.native

import leo.isName
import leo.isTypeLine
import leo.lineTo
import leo.natives.lessName
import leo.natives.thanName
import leo.numberName
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
import kotlin.math.PI

val nativeEnvironment: Environment<Native>
  get() =
    Environment(
      { literal -> literal.native.nativeTerm },
      { typedTerm ->
        when (typedTerm.t) {
          type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                fn(fn(DoublePlusDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                fn(fn(DoubleMinusDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                fn(fn(DoubleTimesDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberName lineTo type("pi")) ->
            typed(
              PI.native.nativeTerm,
              type(numberTypeLine)
            )
          type(numberTypeLine, "equals" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                fn(fn(ObjectEqualsObjectNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(equalsTypeLine)
            )
          type(numberTypeLine, isName lineTo type(lessName lineTo type(thanName lineTo type(numberTypeLine)))) ->
            typed(
              fn(
                fn(fn(DoubleIsLessThanDoubleNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(isTypeLine)
            )
          type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
            typed(
              fn(
                fn(fn(StringPlusStringNative.nativeTerm)).invoke(get<Native>(0).tail).invoke(get<Native>(0).head)
              ).invoke(typedTerm.v),
              type(textTypeLine)
            )
          type("length" lineTo type(textTypeLine)) ->
            typed(
              fn(StringLengthNative.nativeTerm).invoke(typedTerm.v),
              type("length" lineTo type(numberTypeLine))
            )
          else -> null
        }
      },
      { it.scriptLine })
