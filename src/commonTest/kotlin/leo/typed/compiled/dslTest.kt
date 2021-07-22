package leo.typed.compiled

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
      .assertEqualTo("x" lineTo compiled("zero"))

    compiled
      .line(1)
      .assertEqualTo("y" lineTo compiled("one"))

    assertFails {
      compiled.line(2)
    }
  }

  @Test
  fun lineAtIndex_notTuple() {
    val compiled = compiled(
      expression<Unit>(variable(type("foo"))),
      type(
        "x" lineTo type("zero"),
        "y" lineTo type("one")))

    compiled
      .line(0)
      .assertEqualTo(
        compiled(
          line(get(compiled, "x")),
          "x" lineTo type("zero")))

    compiled
      .line(1)
      .assertEqualTo(
        compiled(
          line(get(compiled, "y")),
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
  fun getOrNull_lines() {
    val compiled = compiled(
      expression<Unit>(variable(type("foo"))),
      type(
        "x" lineTo type("zero"),
        "y" lineTo type("one")))

    compiled
      .getOrNull("x")
      .assertEqualTo(compiled.lineCompiled(0))

    compiled
      .getOrNull("y")
      .assertEqualTo(compiled.lineCompiled(1))

    compiled
      .getOrNull("z")
      .assertNull
  }

  @Test
  fun getOrNull_inner() {
    val compiled = compiled(
      expression<Unit>(variable(type("foo"))),
      type(
        "point" lineTo type(
          "x" lineTo type("zero"),
          "y" lineTo type("one"))))

    compiled
      .getOrNull("x")
      .assertEqualTo(compiled.rhs.lineCompiled(0))

    compiled
      .getOrNull("y")
      .assertEqualTo(compiled.rhs.lineCompiled(1))

    compiled
      .getOrNull("z")
      .assertNull
  }

  @Test
  fun getOrNull_twoLevelsInner() {
    val compiled = compiled(
      expression<Unit>(variable(type("foo"))),
      type(
        "my" lineTo type(
          "point" lineTo type(
            "x" lineTo type("zero"),
            "y" lineTo type("one")))))

    compiled
      .getOrNull("x")
      .assertEqualTo(compiled.rhs.rhs.lineCompiled(0))

    compiled
      .getOrNull("y")
      .assertEqualTo(compiled.rhs.rhs.lineCompiled(1))

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
      expression<Unit>(variable(type("foo"))),
      type("x" lineTo type("zero")))

    compiled
      .compiledTupleOrNull
      .assertEqualTo(compiled(tuple(line(get(compiled, "x"))), structure("x" lineTo type("zero"))))
  }

  @Test
  fun indirect() {
    compiled<Unit>("foo")
      .indirect { compiled("bar" lineTo it) }
      .assertEqualTo(
        fn(
          type("foo"),
          compiled("bar" lineTo compiledVariable<Unit>(type("foo"), type("foo"))))
          .invoke(compiled("foo")))
  }

  @Test
  fun have() {
    compiled<Unit>().have(compiled()).assertEqualTo(compiled())
    compiled<Unit>().have(compiled("foo")).assertEqualTo(compiled("foo"))
    compiled<Unit>("foo").have(compiled()).assertEqualTo(compiled("foo"))
    compiled<Unit>("foo").have(compiled("bar")).assertEqualTo(compiled("foo" lineTo compiled("bar")))
    compiled<Unit>("foo" lineTo compiled("zoo")).have(compiled("bar"))
      .assertEqualTo(compiled("foo" lineTo compiled("zoo" lineTo compiled("bar"))))

    compiled<Unit>("foo" lineTo compiled(), "bar" lineTo compiled())
      .haveOrNull(compiled("zoo"))
      .assertNull

    nativeCompiled("foo").haveOrNull(compiled("zoo")).assertNull
  }

  @Test
  fun recursiveRhs() {
    compiled(recursive("foo" lineTo compiled<Nothing>("bar")))
      .rhs
      .assertEqualTo(compiled("bar"))
  }

  @Test
  fun recursiveGet() {
    compiled(recursive("foo" lineTo compiled<Nothing>("bar")))
      .get("bar")
      .assertEqualTo(compiled("bar"))
  }
}
