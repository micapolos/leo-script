package leo.term.compiler

import leo.base.assertEqualTo
import leo.choice
import leo.empty
import leo.function
import leo.lineTo
import leo.term.compiler.native.Native
import leo.term.compiler.native.nativeEnvironment
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.fn
import leo.term.term
import leo.type
import kotlin.test.Test

class ModuleTest {
  @Test
  fun plusCast() {
    nativeEnvironment.context.module
      .plusCast(
        type(
          "my" lineTo type(
            "kleene" lineTo type(
              choice(
                "true" lineTo type(),
                "maybe" lineTo type(),
                "false" lineTo type())))))
      .assertEqualTo(
        nativeEnvironment
          .context
          .module
          .plus(
            binding(
              definition(
                function(
                  type("my" lineTo type("kleene" lineTo type("true"))),
                  type("my" lineTo type("kleene" lineTo type(
                    choice(
                      "true" lineTo type(),
                      "maybe" lineTo type(),
                      "false" lineTo type()))))))))
          .plus(
            binding(
              definition(
                function(
                  type("my" lineTo type("kleene" lineTo type("maybe"))),
                  type("my" lineTo type("kleene" lineTo type(
                    choice(
                      "true" lineTo type(),
                      "maybe" lineTo type(),
                      "false" lineTo type()))))))))
          .plus(
            binding(
              definition(
                function(
                  type("my" lineTo type("kleene" lineTo type("false"))),
                  type("my" lineTo type("kleene" lineTo type(
                    choice(
                      "true" lineTo type(),
                      "maybe" lineTo type(),
                      "false" lineTo type()))))))))
            // TODO: Is it really correct?
          .plus(fn(term<Native>(empty).eitherFirst.eitherFirst))
          .plus(fn(term<Native>(empty).eitherSecond.eitherFirst))
          .plus(fn(term<Native>(empty).eitherSecond)))
  }
}
