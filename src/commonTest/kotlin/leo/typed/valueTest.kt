package leo.typed

import leo.base.assertEqualTo
import leo.base.assertNull
import kotlin.test.Test

class ValueTest {
	@Test
	fun native() {
		10.field.typeFieldOrNull
			.assertEqualTo(numberField)
		"foo".field.typeFieldOrNull
			.assertEqualTo(textField)
	}

	@Test
	fun staticField() {
		"foo".fieldTo(structure()).typeFieldOrNull.assertNull
	}

	@Test
	fun staticStructure() {
		structure(
			"x" fieldTo structure(),
			"y" fieldTo structure())
			.typeStructureOrNull
			.assertNull
	}

	@Test
	fun dynamicStructure() {
		structure(
			"point" fieldTo structure(
				"x" fieldTo structure(10.field),
				"y" fieldTo structure(20.field)))
			.typeStructureOrNull
			.assertEqualTo(
				structure(
					"point" fieldTo structure(
						"x" fieldTo structure(numberField),
						"y" fieldTo structure(numberField))))
	}
}