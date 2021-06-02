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
	fun nativeMatchesKClass() {
		value(textName fieldTo rhs(native("Hello, world!")))
			.matches(type(textName fieldTo rhs(String::class)))
			.assertTrue

		value(textName fieldTo rhs(native("Hello, world!")))
			.matches(type(textName fieldTo rhs(Number::class)))
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
	fun matchesFunction() {
		value(doingName fieldTo rhs(dictionary().function(body(block(syntax())))))
			.matches(type(doingName fieldTo rhs(TypeFunction)))
			.assertTrue
	}
}