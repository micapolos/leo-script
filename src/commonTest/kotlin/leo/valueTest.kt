package leo

import leo.base.assertEqualTo
import leo.base.assertNull
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class ValueTest {
	@Test
	fun values() {
		value("foo")
		value(
			"point" fieldTo value(
				"first" fieldTo value("foo"),
				"last" fieldTo value("bar")
			)
		)
	}

	@Test
	fun dictionaryResolve() {
		dictionary()
			.resolveEvaluation(value("foo")).get
			.assertEqualTo(value("foo"))
	}

	@Test
	fun select() {
		value(
			"x" fieldTo value("zero"),
			"y" fieldTo value("one"),
			"x" fieldTo value("two")
		)
			.run {
				selectOrNull("x").assertEqualTo(value("x" fieldTo value("two")))
				selectOrNull("y").assertEqualTo(value("y" fieldTo value("one")))
				selectOrNull("z").assertEqualTo(null)
			}
	}

	@Test
	fun get() {
		value(
			"point" fieldTo value(
				"x" fieldTo value("10"),
				"y" fieldTo value("20"),
				"x" fieldTo value("30")
			)
		)
			.run {
				getOrNull("x").assertEqualTo(value("x" fieldTo value("30")))
				getOrNull("y").assertEqualTo(value("y" fieldTo value("20")))
				getOrNull("z").assertEqualTo(null)
			}
	}

	@Test
	fun getDeeper() {
		value(
			"center" fieldTo value(
				"point" fieldTo value(
					"x" fieldTo value("10"),
					"y" fieldTo value("20"))))
			.run {
				getOrNull("x").assertEqualTo(value("x" fieldTo value("10")))
				getOrNull("y").assertEqualTo(value("y" fieldTo value("20")))
				getOrNull("z").assertEqualTo(null)
			}
	}

	@Test
	fun resolveFunction() {
		value(field(dictionary().function(binder(doing(body(script("foo")))))))
			.functionOrNull
			.assertEqualTo(dictionary().function(binder(doing((body(script("foo")))))))

		value("function" fieldTo value("foo"))
			.functionOrNull
			.assertEqualTo(null)
	}

	@Test
	fun resolveText() {
		value(field(literal("foo")))
			.textOrNull
			.assertEqualTo("foo")

		value("foo")
			.textOrNull
			.assertEqualTo(null)

		value("text" fieldTo value("foo"))
			.functionOrNull
			.assertEqualTo(null)
	}

	@Test
	fun resolveFunctionApply_content() {
		value(
			field(dictionary().function(binder(doing(body(script("bar")))))),
			giveName fieldTo value(contentName)
		)
			.resolveFunctionApplyOrNullEvaluation
			.get
			.assertEqualTo(value("bar"))
	}

	@Test
	fun matching_ok() {
		value(field(literal("foo")))
			.as_(textAnyValue)
			.assertEqualTo(value(field(literal("foo"))))
	}

	@Test
	fun matching_fail() {
		assertFailsWith<ValueError> {
			value(field(literal(1)))
				.as_(textAnyValue)
		}
	}

	@Test
	fun replaceOrNull() {
		value(
			"x" fieldTo value("zero"),
			"y" fieldTo value("one"),
			"x" fieldTo value("two")
		)
			.run {
				replaceOrThrow("x" fieldTo value("three"))
					.assertEqualTo(
						value(
							"x" fieldTo value("zero"),
							"y" fieldTo value("one"),
							"x" fieldTo value("three")
						)
					)

				replaceOrThrow("y" fieldTo value("three"))
					.assertEqualTo(
						value(
							"x" fieldTo value("zero"),
							"y" fieldTo value("three"),
							"x" fieldTo value("two")
						)
					)

				assertFails {
					replaceOrThrow("z" fieldTo value("three"))
				}
			}
	}

	@Test
	fun setOrNull() {
		value(
			"point" fieldTo value(
				"x" fieldTo value("zero"),
				"y" fieldTo value("one"),
				"x" fieldTo value("two")
			)
		)
			.run {
				setOrThrow(value("x" fieldTo value("three")))
					.assertEqualTo(
						value(
							"point" fieldTo value(
								"x" fieldTo value("zero"),
								"y" fieldTo value("one"),
								"x" fieldTo value("three")
							)
						)
					)

				setOrThrow(value("y" fieldTo value("three")))
					.assertEqualTo(
						value(
							"point" fieldTo value(
								"x" fieldTo value("zero"),
								"y" fieldTo value("three"),
								"x" fieldTo value("two")
							)
						)
					)

				assertFails {
					setOrThrow(value("z" fieldTo value("three")))
				}

				setOrThrow(
					value(
						"x" fieldTo value("three"),
						"y" fieldTo value("four")
					)
				)
					.assertEqualTo(
						value(
							"point" fieldTo value(
								"x" fieldTo value("zero"),
								"y" fieldTo value("four"),
								"x" fieldTo value("three")
							)
						)
					)
			}
	}

	@Test
	fun applyGet() {
		value(
			"point" fieldTo value(
				"x" fieldTo value("zero"),
				"y" fieldTo value("one")))
			.apply(get("x", "y", "y"))
			.assertEqualTo(
				value(
					"x" fieldTo value("zero"),
					"y" fieldTo value("one"),
					"y" fieldTo value("one")))

		assertFailsWith<ValueError> {
			value(
				"point" fieldTo value(
					"x" fieldTo value("zero"),
					"y" fieldTo value("one")))
				.apply(get("z"))
		}
	}

	@Test
	fun selectFieldOrNull_list() {
		value(listName fieldTo value())
			.selectFieldOrNull
			.assertEqualTo(emptyName fieldTo value())

		value(
			listName fieldTo value(
				"x" fieldTo value("zero"),
				"y" fieldTo value("one"),
				"z" fieldTo value("two")))
			.selectFieldOrNull
			.assertEqualTo(
				linkName fieldTo value(
					previousName fieldTo value(
						listName fieldTo value(
							"x" fieldTo value("zero"),
							"y" fieldTo value("one"))),
					lastName fieldTo value(
						"z" fieldTo value("two"))))
	}

	@Test
	fun nthOrNull() {
		val value = value(
			"vector" fieldTo value(
				"x" fieldTo value(field(literal(10))),
				"y" fieldTo value(field(literal(20)))),
			"add" fieldTo value(
				"vector" fieldTo value(
					"x" fieldTo value(field(literal(30))),
					"y" fieldTo value(field(literal(40))))))

		value
			.nthOrNull(1, "vector")
			.assertEqualTo(
				value(
					"vector" fieldTo value(
						"x" fieldTo value(field(literal(10))),
						"y" fieldTo value(field(literal(20))))))

		value
			.nthOrNull(2, "vector")
			.assertEqualTo(
				value(
					"vector" fieldTo value(
						"x" fieldTo value(field(literal(30))),
						"y" fieldTo value(field(literal(40))))))

		value
			.nthOrNull(3, "vector")
			.assertNull

		value.nthOrNull(1, "x").assertNull
		value.nthOrNull(1, "y").assertNull
	}
}