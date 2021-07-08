package leo.term.compiler

import leo.base.assertNotNull
import leo.numberTypeLine
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
import leo.term.nativeTerm
import leo.term.typed.lineTo
import leo.term.typed.typed
import leo.term.typed.typedTerm
import org.junit.Test

class ResolveTypeTest {
  @Test
  fun type() {
    nativeEnvironment.resolveType(
      typedTerm(
        "point" lineTo typedTerm(
          "x" lineTo typedTerm(typed(10.0.native.nativeTerm, numberTypeLine)),
          "y" lineTo typedTerm(typed(10.0.native.nativeTerm, numberTypeLine))
        )
      )
    )
      .assertNotNull // TODO()
  }
}