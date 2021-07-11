package leo.term.compiled

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.functionType
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import scheme.scheme
import kotlin.test.Test

class DslTest {
  @Test
  fun empty() {
    compiled(
      "x" lineTo nativeCompiled(10, type(numberTypeLine)),
      "y" lineTo nativeCompiled(20, type(numberTypeLine)))
      .assertEqualTo(null)
  }

  @Test
  fun apply() {
    nativeCompiled(scheme("num"), type(numberTypeLine))
      .apply(nativeCompiled(scheme("fn"), functionType(type(numberTypeLine), type(textTypeLine))))
      .assertEqualTo(
        compiled(
          expression(
            apply(
              nativeCompiled(scheme("num"), type(numberTypeLine)),
              nativeCompiled(scheme("fn"), functionType(type(numberTypeLine), type(textTypeLine))))),
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
        .apply(nativeCompiled(scheme("fn"), functionType(type(textTypeLine), type(numberTypeLine))))
    }
  }
}