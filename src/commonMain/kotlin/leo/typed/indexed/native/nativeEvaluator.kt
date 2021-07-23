package leo.typed.indexed.native

import leo.typed.compiler.native.DoubleCosinusNative
import leo.typed.compiler.native.DoubleDividedByDoubleNative
import leo.typed.compiler.native.DoubleIsLessThanDoubleNative
import leo.typed.compiler.native.DoubleMinusDoubleNative
import leo.typed.compiler.native.DoubleNative
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.DoubleRootNative
import leo.typed.compiler.native.DoubleSinusNative
import leo.typed.compiler.native.DoubleStringNative
import leo.typed.compiler.native.DoubleTimesDoubleNative
import leo.typed.compiler.native.EDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.ObjectEqualsObjectNative
import leo.typed.compiler.native.PiDoubleNative
import leo.typed.compiler.native.StringLengthNative
import leo.typed.compiler.native.StringNative
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.double
import leo.typed.compiler.native.native
import leo.typed.compiler.native.string
import leo.typed.indexed.Evaluation
import leo.typed.indexed.Evaluator
import leo.typed.indexed.Value
import leo.typed.indexed.ValueScope
import leo.typed.indexed.evaluation
import leo.typed.indexed.native
import leo.typed.indexed.nativeValue
import leo.typed.indexed.value
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

val nativeEvaluator: Evaluator<Native> get() =
  Evaluator(
    { params -> value(*params) },
    { scope -> valueEvaluation(scope) })

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
    DoubleDividedByDoubleNative ->
      nativeValue(params[0].native.double.div(params[1].native.double).native)
    DoubleIsLessThanDoubleNative ->
      value(params[0].native.double < params[1].native.double)
    DoubleStringNative ->
      nativeValue(params[0].native.double.toString().native)
    PiDoubleNative -> error("")
    EDoubleNative -> error("")
    DoubleRootNative ->
      nativeValue(sqrt(params[0].native.double).native)
    DoubleSinusNative ->
      nativeValue(sin(params[0].native.double).native)
    DoubleCosinusNative ->
      nativeValue(cos(params[0].native.double).native)
    ObjectEqualsObjectNative ->
      value(params[0] == params[1])
    StringPlusStringNative ->
      nativeValue(params[0].native.string.plus(params[1].native.string).native)
    StringLengthNative ->
      nativeValue(params[0].native.string.length.toDouble().native)
  }

@Suppress("UNUSED_PARAMETER")
fun Native.valueEvaluation(scope: ValueScope<Native>): Evaluation<Native, Value<Native>> =
  when (this) {
    PiDoubleNative -> nativeValue(PI.native).evaluation()
    EDoubleNative -> nativeValue(E.native).evaluation()
    else -> nativeValue(this).evaluation()
  }
