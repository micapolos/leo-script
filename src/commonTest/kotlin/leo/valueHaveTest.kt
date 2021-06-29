package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueHaveTest {
	@Test
	fun empty() {
		value()
			.have(value("color"))
			.assertEqualTo(value("color"))
	}

	@Test
	fun name() {
		value("color")
			.have(value("red"))
			.assertEqualTo(value("color" fieldTo value("red")))
	}

	@Test
	fun names() {
		value("circle" fieldTo value("color"))
			.have(value("red"))
			.assertEqualTo(value("circle" fieldTo value("color" fieldTo value("red"))))
	}

	@Test
	fun fields() {
		value(
			"circle" fieldTo value(
				"radius" fieldTo value(field(literal(10)))),
				"center" fieldTo value())
			.have(
				value(
					"point" fieldTo value(
						"x" fieldTo value(field(literal(20))),
						"y" fieldTo value(field(literal(30))))))
			.assertEqualTo(
				value(
					"circle" fieldTo value(
						"radius" fieldTo value(field(literal(10)))),
					"center" fieldTo value(
						"point" fieldTo value(
							"x" fieldTo value(field(literal(20))),
							"y" fieldTo value(field(literal(30)))))))
	}
}