package leo.term.compiled.scheme

import leo.base.assertEqualTo
import leo.choice
import leo.lineTo
import leo.numberTypeLine
import leo.term.compiled.apply
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.expression
import leo.term.compiled.field
import leo.term.compiled.function
import leo.term.compiled.get
import leo.term.compiled.nativeLine
import leo.term.compiled.scope
import leo.term.compiled.select
import leo.term.compiled.tuple
import leo.textTypeLine
import leo.type
import scheme.Scheme
import scheme.plus
import scheme.scheme
import kotlin.test.Test

class SchemeTest {
  @Test
  fun native() {
    nativeLine(scheme("a"))
      .scheme(scope())
      .string
      .assertEqualTo("a")
  }

  @Test
  fun field() {
    field(
      "foo",
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine)))
      .scheme(scope())
      .string
      .assertEqualTo("a")
  }

  @Test
  fun get() {
    get(
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine)),
      0)
      .scheme(scope())
      .string
      .assertEqualTo("a")

    get(
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine, numberTypeLine)),
      0)
      .scheme(scope())
      .string
      .assertEqualTo("(cdr a)")

    get(
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine, numberTypeLine)),
      1)
      .scheme(scope())
      .string
      .assertEqualTo("(car a)")

    get(
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine, numberTypeLine, numberTypeLine)),
      2)
      .scheme(scope())
      .string
      .assertEqualTo("(vector-ref a 2)")
  }

  @Test
  fun function() {
    function<Scheme>(type(), body(compiled()))
      .scheme(scope())
      .string
      .assertEqualTo("(lambda () `())")

    function<Scheme>(type(numberTypeLine), body(compiled()))
      .scheme(scope())
      .string
      .assertEqualTo("(lambda (v0) `())")

    function<Scheme>(type(numberTypeLine, textTypeLine), body(compiled()))
      .scheme(scope())
      .string
      .assertEqualTo("(lambda (v0 v1) `())")
  }

  @Test
  fun select() {
    select(choice(numberTypeLine), 0, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(scheme("a"))

    select(choice("a" lineTo type(), "b" lineTo type()), 0, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(true.scheme)

    select(choice("a" lineTo type(), "b" lineTo type()), 1, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(false.scheme)

    select(choice("a" lineTo type(), "b" lineTo type(), "c" lineTo type()), 2, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(scheme(2))

    select(choice(numberTypeLine, numberTypeLine), 0, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(true.scheme.plus(scheme("a")))

    select(choice(numberTypeLine, numberTypeLine), 1, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(false.scheme.plus(scheme("a")))

    select(choice(numberTypeLine, numberTypeLine, numberTypeLine), 2, nativeLine(scheme("a")))
      .scheme(scope())
      .assertEqualTo(scheme(2).plus(scheme("a")))
  }

  @Test
  fun apply_() {
    apply(
      compiled(expression(tuple(nativeLine(scheme("a")))), type(numberTypeLine)),
      compiled(expression(tuple(nativeLine(scheme("b")))), type(numberTypeLine)))
      .scheme(scope())
      .string
      .assertEqualTo("(b a)")
  }
}