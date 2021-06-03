package leo

import leo.base.assertFalse
import leo.base.assertTrue
import kotlin.test.Test

class ValueMatchesTest {
	@Test
	fun nativeMatchesNative() {
		value(textName fieldTo rhs(native("Hello, world!")))
			.matches(type(textName fieldTo typeRhs(native("Hello, world!"))))
			.assertTrue

		value(textName fieldTo rhs(native("Hello, world!")))
			.matches(type(textName fieldTo typeRhs(native("Hello, hell!"))))
			.assertFalse
	}

	@Test
	fun anyMatches() {
		value("foo")
			.matches(anyType())
			.assertTrue

		value(textName fieldTo rhs(native("Hello, world!")))
			.matches(type(textName fieldTo rhs(anyType())))
			.assertTrue

		value(textName fieldTo value("foo"))
			.matches(type(textName fieldTo rhs(anyType())))
			.assertTrue

		value(doingName fieldTo rhs(dictionary().function(body(block(syntax())))))
			.matches(type(doingName fieldTo rhs(anyType())))
			.assertTrue

		value(
			field(literal(10)),
			plusName fieldTo value(field(literal(20))))
			.matches(anyType(plusName fieldTo anyType))
			.assertTrue
	}

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

		value("foo")
			.matches(value(anyName))
			.assertFalse

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
	}
}