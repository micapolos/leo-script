package leo.typed.compiled

import leo.base.assertEqualTo
import leo.base.assertNull
import leo.choice
import leo.line
import leo.lineTo
import leo.recurseTypeLine
import leo.recursive
import leo.recursiveLine
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

  @Test
  fun recursive() {
    compiled<Nothing>("empty")
      .castOrNull(type(recursiveLine("empty" lineTo type())))
      .assertEqualTo(
        compiled(
          expression(tuple(line(field("empty", compiled())))),
          type(line(recursive("empty" lineTo type())))))
  }

  @Test
  fun recursiveChoice() {
    compiled<Nothing>("nat" lineTo compiled("zero"))
      .castOrNull(
        type(
          recursiveLine(
            "nat" lineTo type(
              choice(
                "zero" lineTo type(),
                "previous" lineTo type(recurseTypeLine))))))
      .assertEqualTo(
        compiled(
          expression(
            tuple(
              line(
                field(
                  "nat",
                  selectCompiled(
                    line(the("zero" lineTo compiled())),
                    line(not("previous" lineTo type(recurseTypeLine)))))))),
          type(
            recursiveLine(
              "nat" lineTo type(
                choice(
                  "zero" lineTo type(),
                  "previous" lineTo type(recurseTypeLine)))))))
  }
}