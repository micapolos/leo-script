package leo.term.compiled

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.base.assertNotNull
import leo.functionType
import leo.numberTypeLine
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class DslTest {
  @Test
  fun empty() {
    compiled(
      "x" lineTo nativeCompiled(10, type(numberTypeLine)),
      "y" lineTo nativeCompiled(20, type(numberTypeLine)))
      .assertNotNull
  }

  @Test
  fun apply() {
    nativeCompiled("num", type(numberTypeLine))
      .apply(nativeCompiled("fn", functionType(type(numberTypeLine), type(textTypeLine))))
      .assertEqualTo(
        compiled(
          expression(
            apply(
              nativeCompiled("num", type(numberTypeLine)),
              nativeCompiled("fn", functionType(type(numberTypeLine), type(textTypeLine))))),
          type(textTypeLine)))
  }

  @Test
  fun apply_notFunction() {
    assertFails {
      nativeCompiled("num", type(numberTypeLine))
        .apply(nativeCompiled("fn", type(numberTypeLine)))
    }
  }

  @Test
  fun apply_typeMismatch() {
    assertFails {
      nativeCompiled("num", type(numberTypeLine))
        .apply(nativeCompiled("fn", functionType(type(textTypeLine), type(numberTypeLine))))
    }
  }

  @Test
  fun do_() {
    nativeCompiled("number", type(numberTypeLine))
      .do_(body(nativeCompiled("text", type(textTypeLine))))
      .assertNotNull
  }
}