package leo.term.compiler

import leo.base.assertNotNull
import leo.numberTypeLine
import leo.term.compiled.compiled
import leo.term.compiled.lineTo
import leo.term.compiled.nativeLine
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
import org.junit.Test

class ResolveTypeTest {
  @Test
  fun type() {
    nativeEnvironment.resolveType(
      compiled(
        "point" lineTo compiled(
          "x" lineTo compiled(compiled(nativeLine(10.0.native), numberTypeLine)),
          "y" lineTo compiled(compiled(nativeLine(10.0.native), numberTypeLine))
        )
      )
    )
      .assertNotNull // TODO()
  }
}