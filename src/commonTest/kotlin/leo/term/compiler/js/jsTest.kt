package leo.term.compiler.js

import leo.base.assertEqualTo
import leo.line
import leo.lineTo
import leo.plusName
import leo.script
import org.junit.Test

class JsTest {
  @Test
  fun literal() {
    script(line(leo.literal("Hello, world!")))
      .js
      .string
      .assertEqualTo("\"Hello, world!\"")
  }

  @Test
  fun lines() {
    script(
      "x" lineTo script(leo.literal(10)),
      "y" lineTo script(leo.literal(20))
    )
      .js
      .string
      .assertEqualTo("(v0=>(v1=>(v2=>v2(v0)(v1))))(10)(20)")
  }

  @Test
  fun numberPlusNumber() {
    script(
      line(leo.literal(10)),
      plusName lineTo script(leo.literal(20))
    )
      .js
      .string
      .assertEqualTo("(v0=>(x=>y=>x+y)(v0((v1=>(v2=>v1))))(v0((v1=>(v2=>v2)))))((v0=>(v1=>(v2=>v2(v0)(v1))))(10)(20))")
  }
}