package leo.term.compiled

import leo.base.assertEqualTo
import leo.base.assertFails
import leo.base.assertNotNull
import leo.base.assertNull
import leo.functionType
import leo.lineTo
import leo.numberTypeLine
import leo.structure
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class DslTest {
  @Test
  fun empty() {
    compiled(
      "x" lineTo nativeCompiled(10, type(numberTypeLine)),
      "y" lineTo nativeCompiled(20, type(numberTypeLine)))
      .assertNotNull
  }

  @Test
  fun apply() {
    nativeCompiled("num", type(numberTypeLine))
      .apply(nativeCompiled("fn", functionType(type(numberTypeLine), type(textTypeLine))))
      .assertEqualTo(
        compiled(
          expression(
            apply(
              nativeCompiled("num", type(numberTypeLine)),
              nativeCompiled("fn", functionType(type(numberTypeLine), type(textTypeLine))))),
          type(textTypeLine)))
  }

  @Test
  fun apply_notFunction() {
    assertFails {
      nativeCompiled("num", type(numberTypeLine))
        .apply(nativeCompiled("fn", type(numberTypeLine)))
    }
  }

  @Test
  fun apply_typeMismatch() {
    assertFails {
      nativeCompiled("num", type(numberTypeLine))
        .apply(nativeCompiled("fn", functionType(type(textTypeLine), type(numberTypeLine))))
    }
  }

  @Test
  fun do_() {
    nativeCompiled("number", type(numberTypeLine))
      .do_(body(nativeCompiled("text", type(textTypeLine))))
      .assertNotNull
  }

  @Test
  fun lineAtIndex_tuple() {
    val compiled = compiled(
      "x" lineTo compiled("zero"),
      "y" lineTo compiled<Unit>("one"))

    compiled
      .line(0)
      .assertEqualTo(compiled(line(get(compiled, 0)), "x" lineTo type("zero")))

    compiled
      .line(1)
      .assertEqualTo(compiled(line(get(compiled, 1)), "y" lineTo type("one")))

    assertFails {
      compiled.line(2)
    }
  }

  @Test
  fun lineAtIndex_notTuple() {
    val compiled = compiled(
      expression<Unit>(leo.term.variable(0)),
      type(
        "x" lineTo type("zero"),
        "y" lineTo type("one")))

    compiled
      .line(0)
      .assertEqualTo(
        compiled(
          line(get(compiled, 0)),
          "x" lineTo type("zero")))

    compiled
      .line(1)
      .assertEqualTo(
        compiled(
          line(get(compiled, 1)),
          "y" lineTo type("one")))
  }

  @Test
  fun getOrNull_tuple() {
    val compiled = compiled(
      "point" lineTo compiled(
        "x" lineTo compiled("zero"),
        "y" lineTo compiled<Unit>("one")))

    compiled
      .getOrNull("x")
      .assertEqualTo(compiled(compiled.rhs.line(0)))

    compiled
      .getOrNull("y")
      .assertEqualTo(compiled(compiled.rhs.line(1)))

    compiled.getOrNull("z").assertNull
  }

  @Test
  fun getOrNull_expression() {
    val compiled = compiled(
      expression<Unit>(leo.term.variable(0)),
      type(
        "point" lineTo type(
          "x" lineTo type("zero"),
          "y" lineTo type("one"))))

    compiled
      .getOrNull("y")
      .assertEqualTo(compiled.getOrNull(1))

    compiled
      .getOrNull("x")
      .assertEqualTo(compiled.getOrNull(0))

    compiled
      .getOrNull("z")
      .assertNull
  }

  @Test
  fun compiledTupleOrNull_tuple() {
    compiled(
      "x" lineTo compiled("zero"),
      "y" lineTo compiled<Unit>("one"))
      .compiledTupleOrNull
      .assertEqualTo(
        compiledTuple(
          "x" lineTo compiled("zero"),
          "y" lineTo compiled("one")))
  }

  @Test
  fun compiledTupleOrNull_expression_singleLine() {
    val compiled = compiled(
      expression<Unit>(leo.term.variable(0)),
      type("x" lineTo type("zero")))

    compiled
      .compiledTupleOrNull
      .assertEqualTo(compiled(tuple(line(get(compiled, 0))), structure("x" lineTo type("zero"))))
  }

  @Test
  fun indirect() {
    compiled<Unit>("foo")
      .indirect { compiled("bar" lineTo it) }
      .assertEqualTo(
        fn(
          type("foo"),
          compiled("bar" lineTo compiledVariable<Unit>(0, type("foo"))))
          .invoke(compiled("foo")))
  }
}
