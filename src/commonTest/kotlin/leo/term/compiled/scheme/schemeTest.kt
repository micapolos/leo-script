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
import leo.term.compiled.indexed
import leo.term.compiled.nativeLine
import leo.term.compiled.scope
import leo.term.compiled.select
import leo.term.compiled.tuple
import leo.term.variable
import leo.textTypeLine
import leo.type
import scheme.Scheme
import scheme.pair
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
      .assertEqualTo("(vector-ref a 0)")

    get(
      compiled(
        expression(tuple(nativeLine(scheme("a")))),
        type(numberTypeLine, numberTypeLine)),
      1)
      .scheme(scope())
      .string
      .assertEqualTo("(vector-ref a 1)")
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
    select(choice("a" lineTo type()), indexed(128, nativeLine(scheme("a"))))
      .scheme(scope())
      .assertEqualTo(scheme(128))

    select(choice(numberTypeLine), indexed(128, nativeLine(scheme("a"))))
      .scheme(scope())
      .assertEqualTo(pair(scheme(128), scheme("a")))

    select(choice("a" lineTo type(), "b" lineTo type()), indexed(128, nativeLine(scheme("a"))))
      .scheme(scope())
      .assertEqualTo(scheme(128))

    select(choice(numberTypeLine, numberTypeLine), indexed(128, nativeLine(scheme("a"))))
      .scheme(scope())
      .assertEqualTo(pair(scheme(128), scheme("a")))
  }

  @Test
  fun applyTupleEmpty() {
    apply(
      compiled(expression(tuple()), type()),
      compiled(expression(tuple(nativeLine(scheme("fn")))), type()))
      .scheme(scope(2))
      .string
      .assertEqualTo("(fn)")
  }

  @Test
  fun applyTupleLine() {
    apply(
      compiled(expression(tuple(nativeLine(scheme("l")))), type()),
      compiled(expression(tuple(nativeLine(scheme("fn")))), type()))
      .scheme(scope())
      .string
      .assertEqualTo("(fn l)")
  }

  @Test
  fun applyTupleLines() {
    apply(
      compiled(expression(tuple(nativeLine(scheme("l1")), nativeLine(scheme("l2")))), type()),
      compiled(expression(tuple(nativeLine(scheme("fn")))), type()))
      .scheme(scope())
      .string
      .assertEqualTo("(fn l1 l2)")
  }

  @Test
  fun applyExpressionEmpty() {
    apply(
      compiled(expression<Scheme>(variable(1)), type()),
      compiled(expression(variable(0)), type()))
      .scheme(scope(2))
      .string
      .assertEqualTo("(v1)")
  }

  @Test
  fun applyExpressionLine() {
    apply(
      compiled(expression<Scheme>(variable(1)), type(numberTypeLine)),
      compiled(expression(variable(0)), type()))
      .scheme(scope(2))
      .string
      .assertEqualTo("(v1 v0)")
  }

  @Test
  fun applyExpressionLines() {
    apply(
      compiled(expression<Scheme>(variable(1)), type(numberTypeLine, numberTypeLine)),
      compiled(expression(variable(0)), type()))
      .scheme(scope(2))
      .string
      .assertEqualTo("(apply v1 (vector->list v0))")
  }

  @Test
  fun plus() {
    apply(
      compiled(expression(tuple(nativeLine(scheme("2")), nativeLine(scheme("3")))), type()),
      compiled(expression(tuple(nativeLine(scheme("+")))), type()))
      .scheme(scope())
      .string
      .assertEqualTo("(+ 2 3)")
  }
}