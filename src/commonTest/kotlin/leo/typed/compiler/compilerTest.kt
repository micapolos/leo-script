package leo.typed.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.letName
import leo.lineTo
import leo.script
import leo.typed.compiler.native.nativeCompiler
import leo.typesName
import kotlin.test.Test

class CompilerTest {
  @Test
  fun letType() {
    nativeCompiler
      .plus(
        typesName lineTo script(
          letName lineTo script(
            "ping" lineTo script(),
            doName lineTo script("pong"))))
      .assertEqualTo(
        nativeCompiler.types(
          script(
            letName lineTo script(
              "ping" lineTo script(),
              doName lineTo script("pong")))))
  }
}