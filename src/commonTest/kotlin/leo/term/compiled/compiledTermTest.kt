package leo.term.compiled

import leo.base.assertEqualTo
import leo.functionLineTo
import leo.lineTo
import leo.numberTypeLine
import leo.plusName
import leo.term.compiled.term.term
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.native
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm
import leo.type
import kotlin.test.Test

class CompiledTermTest {
  @Test
  fun functionInvoke() {
    function(numberTypeLine, numberTypeLine) { tuple(DoublePlusDoubleNative.nativeExpression.of(numberTypeLine)) }
      .expression
      .of(type(numberTypeLine, plusName lineTo type(numberTypeLine)) functionLineTo type(numberTypeLine))
      .invoke(
        tuple(
          10.0.native.nativeExpression.of(numberTypeLine),
          20.0.native.nativeExpression.of(plusName lineTo type(numberTypeLine))))
      .term
      .assertEqualTo(
        fn(fn(DoublePlusDoubleNative.nativeTerm))
          .invoke(10.0.native.nativeTerm)
          .invoke(20.0.native.nativeTerm))
  }
}