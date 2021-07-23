package leo.typed.compiler

import leo.base.assertEqualTo
import leo.lineTo
import leo.script
import leo.typed.compiled.compiled
import leo.typed.compiled.lineTo
import leo.typed.compiled.nativeNumberCompiled
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeEnvironment
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeScript
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
            "x" lineTo nativeEnvironment.staticCompiled(nativeNumberType.nativeScript),
            "y" lineTo nativeEnvironment.staticCompiled(nativeNumberType.nativeScript))))
  }
}