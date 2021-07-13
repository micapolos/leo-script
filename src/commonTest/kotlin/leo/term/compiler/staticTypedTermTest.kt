package leo.term.compiler

import leo.anyName
import leo.base.assertEqualTo
import leo.lineTo
import leo.numberName
import leo.numberTypeLine
import leo.script
import leo.term.compiled.compiled
import leo.term.compiled.lineTo
import leo.term.compiled.nativeLine
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
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
            "x" lineTo compiled(compiled(nativeLine(10.0.native), numberTypeLine)),
            "y" lineTo compiled(compiled(nativeLine(20.0.native), numberTypeLine))
          )
        )
      )
      .assertEqualTo(
        compiled(
          "point" lineTo compiled(
            "x" lineTo compiled(anyName lineTo compiled(numberName)),
            "y" lineTo compiled(anyName lineTo compiled(numberName))
          )
        )
      )
  }
}