package leo

import leo.base.assertEqualTo
import leo.base.assertNull
import kotlin.test.Test

class TypeTraversalTest {
  @Test
  fun replaceNonRecursiveOrNull_present() {
    type("foo" lineTo type(recurseTypeLine))
      .replaceNonRecursiveOrNull(recurseTypeLine, "bar" lineTo type())
      .assertEqualTo(type("foo" lineTo type("bar" lineTo type())))
  }

  @Test
  fun replaceNonRecursiveOrNull_absent() {
    type("foo" lineTo type("zoo" lineTo type()))
      .replaceNonRecursiveOrNull(recurseTypeLine, "bar" lineTo type())
      .assertNull
  }

  @Test
  fun replaceNonRecursiveOrNull_recursive() {
    type("foo" lineTo type(line(recursive("zoo" lineTo type()))))
      .replaceNonRecursiveOrNull(recurseTypeLine, "bar" lineTo type())
      .assertNull
  }

  @Test
  fun atom_nonRecursive() {
    line(
      atom(
        "chain" fieldTo type(
          "data" lineTo type("foo"),
          "next" lineTo type(recurseTypeLine)
        )
      )
    )
      .recursible
      .assertEqualTo(
        recursible(
          atom(
            "chain" fieldTo type(
              "data" lineTo type("foo"),
              "next" lineTo type(recurseTypeLine)
            )
          )
        )
      )
  }

  @Test
  fun atom_recursive() {
    line(
      recursive(
        "chain" lineTo type(
          "data" lineTo type("foo"),
          "next" lineTo type(recurseTypeLine)
        )
      )
    )
      .recursible
      .assertEqualTo(
        recursible(
          atom(
            "chain" fieldTo type(
              "data" lineTo type("foo"),
              "next" lineTo type(
                recursiveLine(
                  "chain" lineTo type(
                    "data" lineTo type("foo"),
                    "next" lineTo type(recurseTypeLine))))))))
  }

  @Test
  fun make_withoutRecursion() {
    type("foo" lineTo type("bar" lineTo type()))
      .make("bar")
      .assertEqualTo(type("bar" lineTo type("foo" lineTo type("bar"))))
  }

  @Test
  fun make_withRecursion() {
    type(recursiveLine("foo" lineTo type("bar" lineTo type(recurseTypeLine))))
      .make("bar")
      .assertEqualTo(type(recursiveLine("bar" lineTo type("foo" lineTo type(recurseTypeLine)))))
  }
}