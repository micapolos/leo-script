package leo.term.compiled.scheme

import leo.base.assertEqualTo
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.function
import leo.term.compiled.scope
import leo.type
import scheme.Scheme
import kotlin.test.Test

class SchemeTest {
  @Test
  fun function() {
    function<Scheme>(type(), body(compiled()))
      .scheme(scope())
      .string
      .assertEqualTo("(lambda () `())")
  }
}