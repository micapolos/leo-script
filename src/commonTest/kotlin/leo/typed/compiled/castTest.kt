package leo.typed.compiled

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.choice
import leo.lineTo
import leo.type
import kotlin.test.Test

class CastTest {
  @Test
  fun firstOrThree() {
    compiled<Nothing>("one" lineTo compiled("foo"))
      .castOrNull(
        type(
          choice(
            "one" lineTo type("foo"),
            "two" lineTo type("bar"),
            "three" lineTo type("zoo"))))
      .assertEqualTo(
        compiledSelect<Nothing>()
          .the("one" lineTo compiled("foo"))
          .not("two" lineTo type("bar"))
          .not("three" lineTo type("zoo"))
          .compiled)
  }

  @Test
  fun secondOrThree() {
    compiled<Nothing>("two" lineTo compiled("bar"))
      .castOrNull(
        type(
          choice(
            "one" lineTo type("foo"),
            "two" lineTo type("bar"),
            "three" lineTo type("zoo"))))
      .assertEqualTo(
        compiledSelect<Nothing>()
          .not("one" lineTo type("foo"))
          .the("two" lineTo compiled("bar"))
          .not("three" lineTo type("zoo"))
          .compiled)
  }

  @Test
  fun thirdOrThree() {
    compiled<Nothing>("three" lineTo compiled("zoo"))
      .castOrNull(
        type(
          choice(
            "one" lineTo type("foo"),
            "two" lineTo type("bar"),
            "three" lineTo type("zoo"))))
      .assertEqualTo(
        compiledSelect<Nothing>()
          .not("one" lineTo type("foo"))
          .not("two" lineTo type("bar"))
          .the("three" lineTo compiled("zoo"))
          .compiled)
  }

  @Test
  fun deep() {
    compiled<Nothing>(
      "deep" lineTo compiled(
        "one" lineTo compiled("foo")))
      .castOrNull(
        type(
          "deep" lineTo type(
            choice(
              "one" lineTo type("foo"),
              "two" lineTo type("bar"),
              "three" lineTo type("zoo")))))
      .assertEqualTo(
        compiled(
          "deep" lineTo compiledSelect<Nothing>()
            .the("one" lineTo compiled("foo"))
            .not("two" lineTo type("bar"))
            .not("three" lineTo type("zoo"))
            .compiled))
  }

  @Test
  fun missingCase() {
    compiled<Nothing>("four" lineTo compiled("bar"))
      .castOrNull(
        type(
          choice(
            "one" lineTo type("foo"),
            "two" lineTo type("bar"),
            "three" lineTo type("zoo"))))
      .assertNull
  }

  @Test
  fun mismatchBody() {
    compiled<Nothing>("one" lineTo compiled("bar"))
      .castOrNull(
        type(
          choice(
            "one" lineTo type("foo"),
            "two" lineTo type("bar"),
            "three" lineTo type("zoo"))))
      .assertNull
  }
}