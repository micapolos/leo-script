package leo.term.compiled

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.functionLineTo
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import scheme.scheme
import kotlin.test.Test

class DslTest {
  @Test
  fun apply() {
    nativeCompiled(scheme("num"), type(numberTypeLine))
      .apply(nativeCompiled(scheme("fn"), type(type(numberTypeLine) functionLineTo type(textTypeLine))))
      .assertEqualTo(
        compiled(
          expression(
            apply(
              nativeCompiled(scheme("num"), type(numberTypeLine)),
              nativeCompiled(scheme("fn"), type(type(numberTypeLine) functionLineTo type(textTypeLine))))),
          type(textTypeLine)))
  }

  @Test
  fun apply_notFunction() {
    assertFails {
      nativeCompiled(scheme("num"), type(numberTypeLine))
        .apply(nativeCompiled(scheme("fn"), type(numberTypeLine)))
    }
  }

  @Test
  fun apply_typeMismatch() {
    assertFails {
      nativeCompiled(scheme("num"), type(numberTypeLine))
        .apply(nativeCompiled(scheme("fn"), type(type(textTypeLine) functionLineTo type(numberTypeLine))))
    }
  }
}