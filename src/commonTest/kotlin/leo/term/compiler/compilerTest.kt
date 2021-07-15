package leo.term.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.letName
import leo.lineTo
import leo.script
import leo.term.compiler.native.nativeCompiler
import leo.typeName
import kotlin.test.Test

class CompilerTest {
  @Test
  fun letType() {
    nativeCompiler
      .plus(
        letName lineTo script(
          typeName lineTo script(
            "ping" lineTo script(),
            doName lineTo script("pong"))))
      .assertEqualTo(
        nativeCompiler.letTypeOrNull(
          script(
            typeName lineTo script(
              "ping" lineTo script(),
              doName lineTo script("pong")))))
  }
}