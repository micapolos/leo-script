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
import leo.term.indexed.ValueScope
import leo.term.indexed.native
import leo.term.indexed.nativeValue
import leo.term.indexed.value
import leo.variable

val nativeEvaluator: Evaluator<Native> get() =
  Evaluator { it.value(this) }

fun Native.value(scope: ValueScope<Native>): Value<Native> =
  when (this) {
    is DoubleNative -> nativeValue(this)
    is StringNative -> nativeValue(this)
    DoublePlusDoubleNative ->
      nativeValue(scope.value(variable(1)).native.double.plus(scope.value(variable(0)).native.double).native)
    DoubleMinusDoubleNative ->
      nativeValue(scope.value(variable(1)).native.double.minus(scope.value(variable(0)).native.double).native)
    DoubleTimesDoubleNative ->
      nativeValue(scope.value(variable(1)).native.double.times(scope.value(variable(0)).native.double).native)
    DoubleIsLessThanDoubleNative ->
      value((scope.value(variable(1)).native.double < scope.value(variable(0)).native.double).oneZeroInt)
    ObjectEqualsObjectNative ->
      value((scope.value(variable(1)).native == scope.value(variable(0)).native).oneZeroInt)
    StringPlusStringNative ->
      nativeValue(scope.value(variable(1)).native.string.plus(scope.value(variable(0)).native.string).native)
    StringLengthNative ->
      nativeValue(scope.value(variable(0)).native.string.length.toDouble().native)
  }

val Boolean.oneZeroInt: Int get() = if (this) 0 else 1