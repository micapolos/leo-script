package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class DictionaryTest {
	@Test
	fun applyString() {
		dictionary()
			.plus(
				definition(
					value("ping"),
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
					value("name" fieldTo anythingValue),
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
					anythingValue,
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
					anythingValue.plus("plus" fieldTo anythingValue),
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
					textAnyValue,
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					value(field(literal("foo"))),
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					value(field(literal("foo"))),
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal("bar"))))
			.get
			.assertEqualTo(null)

		dictionary()
			.plus(
				definition(
					value(field(literal(123))),
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal(123))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					value(field(literal(123))),
					binding(value("ok"))
				)
			)
			.applyOrNullEvaluation(value(field(literal(124))))
			.get
			.assertEqualTo(null)
	}
}