package leo.term.indexed.native

import leo.term.compiler.native.DoubleIsLessThanDoubleNative
import leo.term.compiler.native.DoubleMinusDoubleNative
import leo.term.compiler.native.DoubleNative
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.DoubleTimesDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.ObjectEqualsObjectNative
import leo.term.compiler.native.StringLengthNative
import leo.term.compiler.native.StringNative
import leo.term.compiler.native.StringPlusStringNative
import leo.term.compiler.native.double
import leo.term.compiler.native.native
import leo.term.compiler.native.string
import leo.term.indexed.Evaluator
import leo.term.indexed.Value
import leo.term.indexed.native
import leo.term.indexed.nativeValue
import leo.term.indexed.value

val nativeEvaluator: Evaluator<Native> get() =
  Evaluator { value(*it) }

fun Native.value(vararg params: Value<Native>): Value<Native> =
  when (this) {
    is DoubleNative ->
      nativeValue(this)
    is StringNative ->
      nativeValue(this)
    DoublePlusDoubleNative ->
      nativeValue(params[0].native.double.plus(params[1].native.double).native)
    DoubleMinusDoubleNative ->
      nativeValue(params[0].native.double.minus(params[1].native.double).native)
    DoubleTimesDoubleNative ->
      nativeValue(params[0].native.double.times(params[1].native.double).native)
    DoubleIsLessThanDoubleNative ->
      value(params[0].native.double < params[1].native.double)
    ObjectEqualsObjectNative ->
      value(params[0].native == params[1].native)
    StringPlusStringNative ->
      nativeValue(params[0].native.string.plus(params[1].native.string).native)
    StringLengthNative ->
      nativeValue(params[0].native.string.length.toDouble().native)
  }
