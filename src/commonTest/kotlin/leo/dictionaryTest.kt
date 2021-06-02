package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class DictionaryTest {
	@Test
	fun applyString() {
		dictionary()
			.plus(
				definition(
					script("ping").type,
					binding(value("pong"))
				)
			)
			.applyOrNullEvaluation(value("ping"))
			.get
			.assertEqualTo(value("pong"))
	}

	@Test
	fun applyStruct() {
		dictionary()
			.plus(
				definition(
					script("name" lineTo script(anyName)).type,
					binding(value("ok"))
				)
			)
			.run {
				applyOrNullEvaluation(value("name" fieldTo value())).get.assertEqualTo(value("ok"))
				applyOrNullEvaluation(value("name" fieldTo value("michal"))).get.assertEqualTo(value("ok"))
				applyOrNullEvaluation(value("name" fieldTo value(field(literal("Michał"))))).get.assertEqualTo(value("ok"))
			}
	}

	@Test
	fun applyAny() {
		dictionary()
			.plus(
				definition(
					script(anyName).type,
					binding(value("pong"))
				)
			)
			.run {
				applyOrNullEvaluation(value("ping")).get.assertEqualTo(value("pong"))
				applyOrNullEvaluation(value("ping")).get.assertEqualTo(value("pong"))
			}
	}

	@Test
	fun anyValueApply() {
		dictionary()
			.plus(
				definition(
					script(anyName lineTo script(), "plus" lineTo script(anyName)).type,
					binding(value("ok"))
				)
			)
			.run {
				applyOrNullEvaluation(value("a" fieldTo value(), "plus" fieldTo value("b" fieldTo value())))
					.get
					.assertEqualTo(value("ok"))
			}
	}

	@Test
	fun literalApply() {
		dictionary()
			.plus(
				definition(
					script(textName lineTo script(anyName)).type,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					script(literal("foo")).type,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					script(literal("foo")).type,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("bar"))))
			.get
			.assertEqualTo(null)

		dictionary()
			.plus(
				definition(
					script(literal(123)).type,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal(123))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					script(literal(123)).type,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal(124))))
			.get
			.assertEqualTo(null)
	}
}