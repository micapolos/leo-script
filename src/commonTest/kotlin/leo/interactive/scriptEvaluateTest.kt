package leo.interactive

import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.beName
import leo.contentName
import leo.isName
import leo.line
import leo.lineTo
import leo.literal
import leo.natives.appendName
import leo.natives.byName
import leo.natives.dividedName
import leo.natives.lessName
import leo.natives.minusName
import leo.natives.thanName
import leo.natives.timesName
import leo.noName
import leo.numberName
import leo.plusName
import leo.prelude.cosinusName
import leo.prelude.piName
import leo.prelude.rootName
import leo.prelude.sinusName
import leo.script
import leo.textName
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

class ScriptEvaluateTest {
	@Test
	fun empty() {
		script().assertSameAfter { evaluate }
	}

	@Test
	fun name() {
		script("foo").assertSameAfter { evaluate }
	}

	@Test
	fun names() {
		script("red", "color")
			.evaluate
			.assertEqualTo(script("color" lineTo script("red")))
	}

	@Test
	fun text() {
		script(literal("foo")).assertSameAfter { evaluate }
	}

	@Test
	fun field() {
		script("color" lineTo script("red")).assertSameAfter { evaluate }
	}

	@Test
	fun fields() {
		script(
			"x" lineTo script("zero"),
			"y" lineTo script("one")).assertSameAfter { evaluate }
	}

	@Test
	fun be() {
		script(
			"ugly" lineTo script(),
			beName lineTo script("pretty"))
			.evaluate
			.assertEqualTo(script("pretty"))
	}

	@Test
	fun content() {
		script(
			contentName lineTo script(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20)))))
			.evaluate
			.assertEqualTo(
				script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20))))
	}

	@Test
	fun content_fields() {
		script(
			contentName lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))))
			.assertSameAfter { evaluate }
	}

	@Test
	fun content_literal() {
		script(contentName lineTo script(literal(10)))
			.assertSameAfter { evaluate }
	}

	@Test
	fun get() {
		script(
			"x" lineTo script(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20)))))
			.evaluate
			.assertEqualTo(script("x" lineTo script(literal(10))))

		script(
			"y" lineTo script(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20)))))
			.evaluate
			.assertEqualTo(script("y" lineTo script(literal(20))))

		script(
			"z" lineTo script(
				"point" lineTo script(
					"x" lineTo script(literal(10)),
					"y" lineTo script(literal(20)))))
			.assertSameAfter { evaluate }
	}

	@Test
	fun textAppendText() {
		script(
			line(literal("Hello, ")),
			appendName lineTo script(literal("world!")))
			.evaluate
			.assertEqualTo(script(literal("Hello, world!")))
	}

	@Test
	fun numberPlusNumber() {
		script(
			line(literal(10)),
			plusName lineTo script(literal(20)))
			.evaluate
			.assertEqualTo(script(literal(30)))
	}

	@Test
	fun numberMinusNumber() {
		script(
			line(literal(30)),
			minusName lineTo script(literal(20)))
			.evaluate
			.assertEqualTo(script(literal(10)))
	}

	@Test
	fun numberTimesNumber() {
		script(
			line(literal(10)),
			timesName lineTo script(literal(20)))
			.evaluate
			.assertEqualTo(script(literal(200)))
	}

	@Test
	fun numberDividedByNumber() {
		script(
			line(literal(10)),
			dividedName lineTo script(byName lineTo script(literal(5))))
			.evaluate
			.assertEqualTo(script(literal(2)))
	}

	@Test
	fun numberIsLessThanNumber() {
		script(
			line(literal(10)),
			isName lineTo script(lessName lineTo script(thanName lineTo script(literal(5)))))
			.evaluate
			.assertEqualTo(script(isName lineTo script(noName)))
	}

	@Test
	fun numberRoot() {
		script(
			line(literal(25)),
			rootName lineTo script())
			.evaluate
			.assertEqualTo(script(literal(5)))
	}

	@Test
	fun numberSinus() {
		script(
			line(literal(10)),
			sinusName lineTo script())
			.evaluate
			.assertEqualTo(script(literal(sin(10.0))))
	}

	@Test
	fun numberCosinus() {
		script(
			line(literal(10)),
			cosinusName lineTo script())
			.evaluate
			.assertEqualTo(script(literal(cos(10.0))))
	}

	@Test
	fun piNumber() {
		script(piName, numberName)
			.evaluate
			.assertEqualTo(script(literal(Math.PI)))
	}

	@Test
	fun numberTest() {
		script(
			line(literal(10)),
			textName lineTo script())
			.evaluate
			.assertEqualTo(script(literal("10")))
	}
}