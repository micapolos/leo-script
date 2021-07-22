package leo.typed.compiler

import leo.base.assertContains
import leo.base.assertEqualTo
import leo.base.reverse
import leo.beName
import leo.choice
import leo.choiceName
import leo.doName
import leo.haveName
import leo.letName
import leo.lineTo
import leo.numberName
import leo.recursiveLine
import leo.recursiveName
import leo.script
import leo.theName
import leo.type
import leo.typed.compiler.native.nativeCompiler
import leo.typed.compiler.native.nativeNumberType
import leo.typesName
import kotlin.test.Test

class CompilerTest {
  @Test
  fun letType() {
    nativeCompiler
      .plus(
        typesName lineTo script(
          letName lineTo script(
            "ping" lineTo script(),
            doName lineTo script("pong"))))
      .assertEqualTo(
        nativeCompiler.types(
          script(
            letName lineTo script(
              "ping" lineTo script(),
              doName lineTo script("pong")))))
  }

  @Test
  fun types() {
    nativeCompiler
      .plus(
        script(
          typesName lineTo script(
            letName lineTo script(
              "foo" lineTo script(),
              beName lineTo script(
                recursiveName lineTo script("bar"))),
            letName lineTo script(
              "circle" lineTo script(),
              haveName lineTo script(
                "radius" lineTo script(numberName))),
            letName lineTo script(
              "rectangle" lineTo script(),
              haveName lineTo script(
                "width" lineTo script(numberName),
                "height" lineTo script(numberName))),
            letName lineTo script(
              "shape" lineTo script(),
              haveName lineTo script(
                choiceName lineTo script(
                  theName lineTo script("circle"),
                  theName lineTo script("rectangle")))))))
      .block.module.typeSeq.reverse
      .assertContains(
        type(recursiveLine("bar" lineTo type())),
        type("circle" lineTo type("radius" lineTo nativeNumberType)),
        type("rectangle" lineTo type(
          "width" lineTo nativeNumberType,
          "height" lineTo nativeNumberType)),
        type("shape" lineTo type(
          choice(
            "circle" lineTo type("radius" lineTo nativeNumberType),
            "rectangle" lineTo type(
              "width" lineTo nativeNumberType,
              "height" lineTo nativeNumberType)))))
  }
}