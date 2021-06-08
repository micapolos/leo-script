package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNameTest {
	@Test
	fun recursive() {
		line(recursive("foo" lineTo type("bar" lineTo type(line(typeRecurse)))))
			.name
			.assertEqualTo("foo")

		line(recursive("foo" lineTo type("bar" lineTo type(line(typeRecurse)))))
			.atomRecursion
			.map { it.fieldOrNull!! }
			.map { it.rhsType.structureOrNull!!.onlyLineOrNull!! }
			.bind { it.nameRecursion }
			.get(null)
			.assertEqualTo("bar")

		line(recursive("foo" lineTo type("bar" lineTo type(line(typeRecurse)))))
			.atomRecursion
			.bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.nameRecursion }
			.get(null)
			.assertEqualTo("bar")

		line(recursive("foo" lineTo type("bar" lineTo type(line(typeRecurse)))))
			.atomRecursion
			.bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.atomRecursion }
			.bind { it.fieldOrNull!!.rhsType.structureOrNull!!.onlyLineOrNull!!.nameRecursion }
			.get(null)
			.assertEqualTo("foo")
	}
}