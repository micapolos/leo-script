package leo.typed.compiler

import leo.base.assertNotNull
import leo.numberTypeLine
import leo.typed.compiled.compiled
import leo.typed.compiled.lineTo
import leo.typed.compiled.nativeLine
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeEnvironment
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