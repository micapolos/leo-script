package leo.term.compiler

import leo.base.assertEqualTo
import leo.choice
import leo.function
import leo.lineTo
import leo.term.compiler.native.Native
import leo.term.compiler.native.nativeEnvironment
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.fn
import leo.term.get
import leo.type
import kotlin.test.Test

class ModuleTest {
  @Test
  fun plusCast() {
    nativeEnvironment.context.module
      .plusCast(
        type(
          "my" lineTo type(
            "boolean" lineTo type(
              choice(
                "true" lineTo type(),
                "false" lineTo type())))))
      .assertEqualTo(
        nativeEnvironment
          .context
          .module
          .plus(
            binding(
              definition(
                function(
                  type("my" lineTo type("boolean" lineTo type("true"))),
                  type("my" lineTo type("boolean" lineTo type(
                    choice(
                      "true" lineTo type(),
                      "false" lineTo type()))))))))
          .plus(
            binding(
              definition(
                function(
                  type("my" lineTo type("boolean" lineTo type("false"))),
                  type("my" lineTo type("boolean" lineTo type(
                    choice(
                      "true" lineTo type(),
                      "false" lineTo type()))))))))
          .plus(fn(fn(get<Native>(0)).eitherFirst))
          .plus(fn(fn(get<Native>(0)).eitherSecond)))
  }
}
