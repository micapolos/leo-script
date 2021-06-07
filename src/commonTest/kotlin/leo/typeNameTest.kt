package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class TypeNameTest {
	@Test
	fun recursive() {
		line(recursive(atom("foo" fieldTo type("bar" lineTo type(line(typeRecurse))))))
			.name
			.assertEqualTo("foo")

		line(recursive(atom("foo" fieldTo type("bar" lineTo type(line(typeRecurse))))))
			.atomRecursion
			.map { it.fieldOrNull!! }
			.map { it.type.structureOrNull!!.onlyLineOrNull!! }
			.bind { it.nameRecursion }
			.get(null)
			.assertEqualTo("bar")

		line(recursive(atom("foo" fieldTo type("bar" lineTo type(line(typeRecurse))))))
			.atomRecursion
			.bind { it.fieldOrNull!!.type.structureOrNull!!.onlyLineOrNull!!.nameRecursion }
			.get(null)
			.assertEqualTo("bar")

		line(recursive(atom("foo" fieldTo type("bar" lineTo type(line(typeRecurse))))))
			.atomRecursion
			.bind { it.fieldOrNull!!.type.structureOrNull!!.onlyLineOrNull!!.atomRecursion }
			.bind { it.fieldOrNull!!.type.structureOrNull!!.onlyLineOrNull!!.nameRecursion }
			.get(null)
			.assertEqualTo("foo")
	}
}