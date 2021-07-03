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
			.matches(anythingValue)
			.assertTrue

		value(field(literal("Hello, world!")))
			.matches(value(textName fieldTo anythingValue))
			.assertTrue

		value(field(literal(10)))
			.matches(value(numberName fieldTo anythingValue))
			.assertTrue

		value(doingName fieldTo rhs(dictionary().function(binder(doing(body(block(syntax())))))))
			.matches(value(doingName fieldTo anythingValue))
			.assertTrue

		value(
			field(literal(10)),
			plusName fieldTo value(field(literal(20))))
			.matches(anythingValue.plus(plusName fieldTo anythingValue))
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
					numberName fieldTo anythingValue,
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertTrue

		value(field(literal("foo")))
			.matches(
				value(
					numberName fieldTo anythingValue,
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertTrue

		value("foo")
			.matches(
				value(
					numberName fieldTo anythingValue,
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertFalse

		value(field(literal(10)))
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anythingValue),
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertTrue

		value(field(literal("foo")))
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anythingValue),
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertTrue

		value("foo")
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anythingValue),
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertFalse

		value()
			.matches(
				value(
					orName fieldTo value(numberName fieldTo anythingValue),
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertFalse

		value()
			.matches(
				value(
					orName fieldTo value(),
					orName fieldTo value(textName fieldTo anythingValue)))
			.assertTrue
	}

	@Test
	fun special() {
		value(field(literal(10))).matches(value(anyName fieldTo value(numberName))).assertTrue
		value(field(literal("foo"))).matches(value(anyName fieldTo value(textName))).assertTrue
	}
}