package leo.term.compiler

import leo.anyName
import leo.base.assertEqualTo
import leo.lineTo
import leo.numberName
import leo.numberTypeLine
import leo.script
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
import leo.term.id
import leo.term.nativeTerm
import leo.term.typed.lineTo
import leo.term.typed.typed
import leo.term.typed.typedTerm
import leo.type
import kotlin.test.Test

class StaticTypedTermTest {
  @Test
  fun staticTypedTerm() {
    nativeEnvironment
      .staticTypedTerm(
        script(
          "point" lineTo script(
            "x" lineTo script("foo"),
            "y" lineTo script("bar")
          )
        )
      )
      .assertEqualTo(
        typed(
          id(),
          type(
            "point" lineTo type(
              "x" lineTo type("foo"),
              "y" lineTo type("bar")
            )
          )
        )
      )
  }

  @Test
  fun resolveType() {
    nativeEnvironment
      .resolveType(
        typedTerm(
          "point" lineTo typedTerm(
            "x" lineTo typedTerm(typed(10.0.native.nativeTerm, numberTypeLine)),
            "y" lineTo typedTerm(typed(20.0.native.nativeTerm, numberTypeLine))
          )
        )
      )
      .assertEqualTo(
        typedTerm(
          "point" lineTo typedTerm(
            "x" lineTo typedTerm(anyName lineTo typedTerm(numberName)),
            "y" lineTo typedTerm(anyName lineTo typedTerm(numberName))
          )
        )
      )
  }
}