package leo.term.indexed.scheme

import leo.base.assertEqualTo
import leo.empty
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.get
import leo.term.indexed.ifThenElse
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.term.indexed.recursive
import leo.term.indexed.switch
import leo.variable
import scheme.Scheme
import scheme.nilScheme
import scheme.scheme
import kotlin.test.Test

class ExpressionValueTest {
  @Test
  fun empty() {
    expression<Scheme>(empty)
      .scheme
      .assertEqualTo(nilScheme)
  }

  @Test
  fun native() {
    nativeExpression(scheme("foo"))
      .scheme
      .assertEqualTo(scheme("foo"))
  }

  @Test
  fun boolean() {
    expression<Scheme>(true)
      .scheme
      .assertEqualTo(scheme("#t"))

    expression<Scheme>(false)
      .scheme
      .assertEqualTo(scheme("#f"))
  }

  @Test
  fun tuple() {
    expression(nativeExpression(scheme("a")), nativeExpression(scheme("b")))
      .scheme
      .assertEqualTo(scheme("(vector a b)"))
  }

  @Test
  fun get() {
    nativeExpression(scheme("a"))
      .get(123)
      .scheme
      .assertEqualTo(scheme("(vector-ref a 123)"))
  }

  @Test
  fun function() {
    expression<Scheme>(function(2, expression(expression(variable(1)), expression(variable(0)))))
      .scheme
      .assertEqualTo(scheme("(lambda (v0 v1) (vector v0 v1))"))
  }

  @Test
  fun invoke() {
    nativeExpression(scheme("fn"))
      .invoke(nativeExpression(scheme("a")), nativeExpression(scheme("b")))
      .scheme
      .assertEqualTo(scheme("(fn a b)"))
  }

  @Test
  fun recursive() {
    expression<Scheme>(recursive(function(2, expression(expression(variable(1)), expression(variable(0))))))
      .scheme
      .assertEqualTo(scheme("(letrec ((v0 (lambda (v1 v2) (vector v1 v2)))) v0)"))
  }

  @Test
  fun index() {
    expression<Scheme>(0)
      .scheme
      .assertEqualTo(scheme("0"))

    expression<Scheme>(1)
      .scheme
      .assertEqualTo(scheme("1"))
  }

  @Test
  fun ifThenElse() {
    nativeExpression(scheme("b"))
      .ifThenElse(
        nativeExpression(scheme("x")),
        nativeExpression(scheme("y")))
      .scheme
      .assertEqualTo(scheme("(if b x y)"))
  }

  @Test
  fun switch() {
    nativeExpression(scheme("i"))
      .switch(
        nativeExpression(scheme("a")),
        nativeExpression(scheme("b")))
      .scheme
      .assertEqualTo(scheme("(case i (0 a) (1 b))"))
  }

  @Test
  fun plus() {
    nativeExpression(scheme("+"))
      .invoke(
        nativeExpression(scheme("10")),
        nativeExpression(scheme("20")))
      .scheme
      .assertEqualTo(scheme("(+ 10 20)"))
  }

  @Test
  fun fibonacci() {
    expression(
      recursive(
        function(1,
          nativeExpression(scheme("<"))
            .invoke(expression(variable(0)), nativeExpression(scheme("2")))
            .ifThenElse(
              expression(variable(0)),
              nativeExpression(scheme("+"))
                .invoke(
                  expression<Scheme>(variable(1))
                    .invoke(
                      nativeExpression(scheme("-"))
                        .invoke(
                          expression(variable(0)),
                          nativeExpression(scheme("2")))),
                  expression<Scheme>(variable(1))
                    .invoke(
                      nativeExpression(scheme("-"))
                        .invoke(
                          expression(variable(0)),
                          nativeExpression(scheme("1")))))))))
      .invoke(nativeExpression(scheme("10")))
      .scheme
      .assertEqualTo(scheme("((letrec ((v0 (lambda (v1) (if (< v1 2) v1 (+ (v0 (- v1 2)) (v0 (- v1 1))))))) v0) 10)"))
  }
}