package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNameTest {
  @Test
  fun recursive() {
    line(recursive("foo" lineTo type("bar" lineTo type(recurseTypeLine))))
      .nameOrNull
      .assertEqualTo("foo")

    line(recursive("foo" lineTo type("bar" lineTo type(recurseTypeLine))))
      .atomRecursion
      .map { it.fieldOrNull!! }
      .map { it.rhsType.structureOrNull!!.onlyLineOrNull!! }
      .bind { it.nameOrNullRecursion }
      .get(null)
      .assertEqualTo("bar")

    line(recursive("foo" lineTo type("bar" lineTo type(recurseTypeLine))))
      .atomRecursion
      .bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.nameOrNullRecursion }
      .get(null)
      .assertEqualTo("bar")

    line(recursive("foo" lineTo type("bar" lineTo type(recurseTypeLine))))
      .atomRecursion
      .bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.atomRecursion }
      .bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.nameOrNullRecursion }
      .get(null)
      .assertEqualTo("foo")
  }
}