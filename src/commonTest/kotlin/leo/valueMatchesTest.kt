package leo

import leo.base.assertFalse
import leo.base.assertTrue
import kotlin.test.Test

class ValueMatchesTest {
	@Test
	fun literalMatchesLiteral() {
		value(field(literal("foo")))
			.matches(value(field(literal("foo"))))
			.assertTrue

		value(field(literal("foo")))
			.matches(value(field(literal("bar"))))
			.assertFalse

		value(field(literal(10)))
			.matches(value(field(literal(10))))
			.assertTrue

		value(field(literal(10)))
			.matches(value(field(literal(11))))
			.assertFalse

		value(field(literal(10)))
			.matches(value(field(literal("Hello"))))
			.assertFalse
	}

	@Test
	fun matchesAny() {
		value("foo")
			.matches(anyValue)
			.assertTrue

		value(field(literal("Hello, world!")))
			.matches(value(textName fieldTo anyValue))
			.assertTrue

		value(field(literal(10)))
			.matches(value(numberName fieldTo anyValue))
			.assertTrue

		value(doingName fieldTo rhs(dictionary().function(body(block(syntax())))))
			.matches(value(doingName fieldTo anyValue))
			.assertTrue

		value(
			field(literal(10)),
			plusName fieldTo value(field(literal(20))))
			.matches(anyValue.plus(plusName fieldTo anyValue))
			.assertTrue

		value(numberName fieldTo value())
			.matches(value(numberAnyField))
			.assertTrue
	}

	@Test
	fun matchesOr() {
		value(field(literal(10)))
			.matches(
				value(
					numberName fieldTo anyValue,
					orName fieldTo value(textName fieldTo anyValue)))
			.assertTrue

		value(field(literal("foo")))
			.matches(
				value(
					numberName fieldTo anyValue,
					orName fieldTo value(textName fieldTo anyValue)))
			.assertTrue

		value("foo")
			.matches(
				value(
					numberName fieldTo anyValue,
					orName fieldTo value(textName fieldTo anyValue)))
			.assertFalse

		value(field(literal(10)))
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anyValue),
					orName fieldTo value(textName fieldTo anyValue)))
			.assertTrue

		value(field(literal("foo")))
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anyValue),
					orName fieldTo value(textName fieldTo anyValue)))
			.assertTrue

		value("foo")
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anyValue),
					orName fieldTo value(textName fieldTo anyValue)))
			.assertFalse

		value()
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anyValue),
					orName fieldTo value(textName fieldTo anyValue)))
			.assertFalse

		value()
			.matches(
				value(
					orName fieldTo value(),
					orName fieldTo value(textName fieldTo anyValue)))
			.assertTrue
	}
}