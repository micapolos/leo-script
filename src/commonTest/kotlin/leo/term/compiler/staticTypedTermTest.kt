package leo.term.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.script
import leo.term.compiled.compiled
import leo.term.compiled.lineTo
import leo.term.compiled.nativeNumberCompiled
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
import leo.term.compiler.native.nativeNumberType
import kotlin.test.Test

class StaticTypedTermTest {
  @Test
  fun staticTypedTerm() {
    nativeEnvironment
      .staticCompiled(
        script(
          "point" lineTo script(
            "x" lineTo script("foo"),
            "y" lineTo script("bar")
          )
        )
      )
      .assertEqualTo(
        compiled(
          "point" lineTo compiled(
              "x" lineTo compiled("foo"),
              "y" lineTo compiled("bar"))))
  }

  @Test
  fun resolveType() {
    nativeEnvironment
      .resolveType(
        compiled(
          "point" lineTo compiled(
            "x" lineTo nativeNumberCompiled(10.0.native),
            "y" lineTo nativeNumberCompiled(20.0.native))))
      .assertEqualTo(
        compiled(
          "point" lineTo compiled(
            "x" lineTo nativeEnvironment.staticCompiled(nativeNumberType.script),
            "y" lineTo nativeEnvironment.staticCompiled(nativeNumberType.script))))
  }
}