package leo.typed.indexed.native

import leo.typed.compiler.native.DoubleIsLessThanDoubleNative
import leo.typed.compiler.native.DoubleMinusDoubleNative
import leo.typed.compiler.native.DoubleNative
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.DoubleStringNative
import leo.typed.compiler.native.DoubleTimesDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.ObjectEqualsObjectNative
import leo.typed.compiler.native.PiDoubleNative
import leo.typed.compiler.native.StringLengthNative
import leo.typed.compiler.native.StringNative
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.double
import leo.typed.compiler.native.native
import leo.typed.compiler.native.string
import leo.typed.indexed.Evaluator
import leo.typed.indexed.Value
import leo.typed.indexed.native
import leo.typed.indexed.nativeValue
import leo.typed.indexed.value
import kotlin.math.PI

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
    DoubleStringNative ->
      nativeValue(params[0].native.double.toString().native)
    PiDoubleNative ->
      nativeValue(PI.native)
    ObjectEqualsObjectNative ->
      value(params[0] == params[1])
    StringPlusStringNative ->
      nativeValue(params[0].native.string.plus(params[1].native.string).native)
    StringLengthNative ->
      nativeValue(params[0].native.string.length.toDouble().native)
  }
