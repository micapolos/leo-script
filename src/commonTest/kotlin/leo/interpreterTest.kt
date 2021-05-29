package leo

import leo.base.assertEqualTo
import leo.base.assertNotNull
import leo.natives.minusName
import kotlin.test.Test

class InterpreterTest {
	@Test
	fun literal() {
		script(line(literal("ok")))
			.interpret
			.assertEqualTo(script(line(literal("ok"))))
	}

	@Test
	fun name() {
		script("ok")
			.interpret
			.assertEqualTo(script("ok"))
	}

	@Test
	fun field() {
		script("foo" lineTo script("bar"))
			.interpret
			.assertEqualTo(script("foo" lineTo script("bar")))
	}

	@Test
	fun lines() {
		script(
			"foo" lineTo script("bar"),
			"zoo" lineTo script("zar")
		)
			.interpret
			.assertEqualTo(
				script(
					"foo" lineTo script("bar"),
					"zoo" lineTo script("zar")
				)
			)
	}

	@Test
	fun struct() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")
			)
		)
			.interpret
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("one")
					)
				)
			)
	}

	@Test
	fun make_implicit() {
		script(
			"red" lineTo script(),
			"color" lineTo script()
		)
			.interpret
			.assertEqualTo(script("color" lineTo script("red")))
	}

	@Test
	fun get_implicit() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")
			),
			"x" lineTo script()
		)
			.interpret
			.assertEqualTo(script("x" lineTo script("zero")))

		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")
			),
			"y" lineTo script()
		)
			.interpret
			.assertEqualTo(script("y" lineTo script("one")))
	}

	@Test
	fun get() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")
			),
			"x" lineTo script()
		)
			.interpret
			.assertEqualTo(script("x" lineTo script("zero")))

		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")
			),
			"y" lineTo script()
		)
			.interpret
			.assertEqualTo(script("y" lineTo script("one")))

		script(
			"x" lineTo script("zero"),
			"y" lineTo script("one"),
			"point" lineTo script()
		)
			.interpret
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("one")
					)
				)
			)
	}

	@Test
	fun function() {
		script("function" lineTo script("foo"))
			.interpret
			.assertEqualTo(script("function" lineTo script("foo")))
	}

	@Test
	fun apply() {
		script(
			doingName lineTo script("name"),
			giveName lineTo script("name" lineTo script("foo"))
		)
			.interpret
			.assertEqualTo(script("name" lineTo script("foo")))
	}

	@Test
	fun take() {
		script(
			"name" lineTo script("foo"),
			takeName lineTo script(doingName lineTo script("name"))
		)
			.interpret
			.assertEqualTo(script("name" lineTo script("foo")))
	}

	@Test
	fun apply_error() {
		script(
			"foo" lineTo script(),
			giveName lineTo script("bar")
		)
			.interpret
			.assertEqualTo(
				script(
					errorName lineTo script(
						"foo" lineTo script(),
						"not" lineTo script("function")
					)
				)
			)
	}

	@Test
	fun functionGet() {
		script(
			"map" lineTo script(doingName lineTo script("foo")),
			doingName lineTo script()
		)
			.interpret
			.assertEqualTo(script(doingName lineTo script("foo")))
	}

	@Test
	fun letEmpty() {
		script(
			"foo" lineTo script(),
			letName lineTo script("bar")
		)
			.interpret
			.assertEqualTo(
				script(
					"foo" lineTo script(),
					letName lineTo script("bar")
				)
			)
	}

	@Test
	fun letDo() {
		script(
			letName lineTo script(
				"name" lineTo script(anyName),
				doName lineTo script("name" lineTo script())
			),
			"name" lineTo script("foo")
		)
			.interpret
			.assertEqualTo(script("name" lineTo script("foo")))
	}

	@Test
	fun letBe() {
		script(
			letName lineTo script(
				"name" lineTo script(),
				beName lineTo script(literal("foo"))
			),
			"name" lineTo script()
		)
			.interpret
			.assertEqualTo(script(literal("foo")))
	}

	@Test
	fun letName() {
		script(
			letName lineTo script(
				numberName lineTo script(anyName),
				"increment" lineTo script(),
				doName lineTo script(
					numberName lineTo script(),
					"plus" lineTo script("one")
				)
			),
			line(literal(2)),
			"increment" lineTo script()
		)
			.interpret
			.assertEqualTo(
				script(
					line(literal(2)),
					"plus" lineTo script("one")
				)
			)
	}

	@Test
	fun switch() {
		script(
			"the" lineTo script(literal("Hello, world!")),
			switchName lineTo script(
				"text" lineTo script("one"),
				"number" lineTo script("two")
			)
		)
			.interpret
			.assertEqualTo(script("one" lineTo script(literal("Hello, world!"))))

		script(
			"the" lineTo script(literal(1)),
			switchName lineTo script(
				"text" lineTo script("one"),
				"number" lineTo script("two")
			)
		)
			.interpret
			.assertEqualTo(script("two" lineTo script(literal(1))))
	}

	@Test
	fun doRepeating() {
		script(
			"number" lineTo script("one"),
			doName lineTo script(
				repeatingName lineTo script(
					numberName lineTo script(),
					switchName lineTo script(
						"zero" lineTo script(
							doName lineTo script(line(literal("OK")))
						),
						"one" lineTo script(
							doName lineTo script(
								numberName lineTo script("zero"),
								repeatName lineTo script(),
							)
						)
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script(line(literal("OK"))))
	}

	@Test
	fun doRepeatingLong() {
		script(
			line(literal(10000)),
			doName lineTo script(
				repeatingName lineTo script(
					numberName lineTo script(),
					isName lineTo script(equalName lineTo script(line(literal(0)))),
					switchName lineTo script(
						yesName lineTo script(doName lineTo script(line(literal("OK")))),
						noName lineTo script(
							doName lineTo script(
								numberName lineTo script(),
								minusName lineTo script(line(literal(1))),
								repeatName lineTo script(),
							)
						)
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script(line(literal("OK"))))
	}

	@Test
	fun doRecursing() {
		script(
			"number" lineTo script("one"),
			doName lineTo script(
				recursingName lineTo script(
					numberName lineTo script(),
					switchName lineTo script(
						"zero" lineTo script(
							doName lineTo script(line(literal("OK")))
						),
						"one" lineTo script(
							doName lineTo script(
								numberName lineTo script("zero"),
								recurseName lineTo script()
							)
						)
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script(line(literal("OK"))))
	}

	@Test
	fun quote() {
		script(
			"foo" lineTo script(),
			quoteName lineTo script(hashName)
		)
			.interpret
			.assertEqualTo(
				script(
					"foo" lineTo script(),
					hashName lineTo script()
				)
			)
	}

	@Test
	fun evaluate() {
		script(
			quoteName lineTo script(hashName),
			evaluateName lineTo script()
		)
			.interpret
			.assertEqualTo(script(hashName lineTo script(line(literal(value().hashCode())))))
	}

	@Test
	fun evaluate_with() {
		script(
			quoteName lineTo script(
				line(literal("Hello, world!")),
				"ok" lineTo script()
			),
			evaluateName lineTo script(literal("world!"))
		)
			.interpret
			.assertEqualTo(script("ok" lineTo script(literal("Hello, world!"))))
	}

	@Test
	fun comment() {
		script(
			commentName lineTo script("first" lineTo script("number")),
			line(literal(2)),
			commentName lineTo script("second" lineTo script("number")),
			"increment" lineTo script(),
			commentName lineTo script("expecting" lineTo script(literal(5)))
		)
			.interpret
			.assertEqualTo(script("increment" lineTo script(literal(2))))
	}

	@Test
	fun be() {
		script(
			"zero" lineTo script(),
			beName lineTo script("one")
		)
			.interpret
			.assertEqualTo(script("one"))
	}

	@Test
	fun set() {
		script(
			""
		)
	}

	@Test
	fun private() {
		script(
			privateName lineTo script(
				letName lineTo script(
					"ping" lineTo script(),
					doName lineTo script("pong")
				)
			),
			"ping" lineTo script()
		)
			.interpret
			.assertEqualTo(script("pong"))
	}

	@Test
	fun private_double() {
		script(
			privateName lineTo script(
				privateName lineTo script(
					letName lineTo script(
						"ping" lineTo script(),
						doName lineTo script("pong")
					)
				)
			),
			"ping" lineTo script()
		)
			.interpret
			.assertEqualTo(script("ping"))
	}

	@Test
	fun use() {
		script(useName lineTo script("lib" lineTo script("text")))
			.interpret
			.assertEqualTo(script(errorName lineTo script(literal("lib/text.leo (No such file or directory)"))))
	}

	@Test
	fun error_native() {
		script(
			textName lineTo script("hello"),
			plusName lineTo script(textName lineTo script("world"))
		)
			.interpret
			.assertNotNull // TODO: Check for error.
	}

	@Test
	fun trace() {
		environment(traceOrNull = emptyTrace)
			.interpret(
				script(
					line(literal("Hello, ")),
					plusName lineTo script(line(literal("world!"))),
					traceName lineTo script()
				)
			)
			.assertEqualTo(
				script(
					traceName lineTo script(
						resolveName lineTo script(literal("Hello, ")),
						resolveName lineTo script(literal("world!")),
						resolveName lineTo script(
							line(literal("Hello, ")),
							plusName lineTo script(line(literal("world!")))
						)
					)
				)
			)
	}

	@Test
	fun trace_disabled() {
		environment()
			.interpret(
				script(
					line(literal("Hello, ")),
					plusName lineTo script(line(literal("world!"))),
					traceName lineTo script()
				)
			)
			.assertEqualTo(script(traceName lineTo script(disabledName)))
	}

	@Test
	fun getHash() {
		script(
			"foo" lineTo script(),
			hashName lineTo script()
		)
			.interpret
			.assertEqualTo(script(hashName lineTo script(literal(value("foo").hashCode()))))
	}

	@Test
	fun isEqual() {
		script(
			"foo" lineTo script(),
			isName lineTo script(equalName lineTo script("foo"))
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(yesName)))

		script(
			"foo" lineTo script(),
			isName lineTo script(equalName lineTo script("bar"))
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(noName)))

		script(
			line(literal("foo")),
			isName lineTo script(equalName lineTo script(line(literal("foo"))))
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(yesName)))
	}

	@Test
	fun isNotEqual() {
		script(
			"foo" lineTo script(),
			isName lineTo script(notName lineTo script(equalName lineTo script("bar")))
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(yesName)))
	}


	@Test
	fun isNotNotEqual() {
		script(
			"foo" lineTo script(),
			isName lineTo script(
				notName lineTo script(
					notName lineTo script(
						equalName lineTo script("bar")
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(noName)))
	}

	@Test
	fun fail() {
		script(
			line("frog"),
			"fail" lineTo script("kiss"))
			.interpret
			.assertEqualTo(script(errorName lineTo script("kiss" lineTo script("frog"))))
	}

	@Test
	fun try_success() {
		script(
			"foo" lineTo script(),
			tryName lineTo script("bar")
		)
			.interpret
			.assertEqualTo(script(tryName lineTo script(successName lineTo script("bar" lineTo script("foo")))))
	}

	@Test
	fun try_error() {
		script(
			"foo" lineTo script(),
			tryName lineTo script(line("bar"), line("fail"))
		)
			.interpret
			.assertEqualTo(script(tryName lineTo script(errorName lineTo script("bar" lineTo script("foo")))))
	}

	@Test
	fun testIsEqualSuccess() {
		script(
			testName lineTo script(
				"foo" lineTo script(),
				"bar" lineTo script(),
				isName lineTo script(
					equalName lineTo script(
						"foo" lineTo script(),
						"bar" lineTo script()
					)
				)
			)
		)
			.interpret
			.assertEqualTo(script())
	}

	@Test
	fun testIsMatchingSuccess() {
		script(
			testName lineTo script(
				"foo" lineTo script("bar"),
				isName lineTo script(
					matchingName lineTo script("foo" lineTo script(anyName))
				)
			)
		)
			.interpret
			.assertEqualTo(script())
	}

	@Test
	fun test_error() {
		script(
			testName lineTo script(
				"foo" lineTo script(),
				"bar" lineTo script(),
				isName lineTo script(
					equalName lineTo script(
						"zoo" lineTo script(),
						"zar" lineTo script()
					)
				)
			)
		)
			.interpret
			.assertEqualTo(
				script(
					errorName lineTo script(
						testName lineTo script(
							"foo" lineTo script(),
							"bar" lineTo script(),
							isName lineTo script(
								equalName lineTo script(
									"zoo" lineTo script(),
									"zar" lineTo script()
								)
							)
						),
						causeName lineTo script(
							"bar" lineTo script("foo"),
							isName lineTo script(
								notName lineTo script(
									equalName lineTo script("zar" lineTo script("zoo"))
								)
							)
						)
					)
				)
			)
	}

	@Test
	fun isMatching() {
		script(
			line(literal("foo")),
			isName lineTo script(
				matchingName lineTo script(textName lineTo script(anyName))
			)
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(yesName)))

		script(
			line(literal("doo")),
			isName lineTo script(matchingName lineTo script(numberName lineTo script(anyName)))
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(noName)))
	}

	@Test
	fun isNotMatching() {
		script(
			line(literal("foo")),
			isName lineTo script(
				notName lineTo script(
					matchingName lineTo script(textName lineTo script(anyName))
				)
			)
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(noName)))

		script(
			line(literal("doo")),
			isName lineTo script(
				notName lineTo script(
					matchingName lineTo script(numberName lineTo script(anyName))
				)
			)
		)
			.interpret
			.assertEqualTo(script(isName lineTo script(yesName)))
	}

	@Test
	fun valueText() {
		script(
			"foo" lineTo script("bar"),
			line(valueName),
			line(textName)
		)
			.interpret
			.assertEqualTo(script(literal("foo bar\n")))
	}

	@Test
	fun textValue() {
		script(
			line(literal("foo bar\n")),
			line(valueName)
		)
			.interpret
			.assertEqualTo(script(valueName lineTo script("foo" lineTo script("bar"))))
	}

	@Test
	fun with() {
		script(
			"x" lineTo script("zero"),
			withName lineTo script(
				"y" lineTo script("one"),
				"z" lineTo script("two")
			)
		)
			.interpret
			.assertEqualTo(
				script(
					"x" lineTo script("zero"),
					"y" lineTo script("one"),
					"z" lineTo script("two")
				)
			)
	}

	@Test
	fun withEmpty() {
		script(
			"x" lineTo script("zero"),
			withName lineTo script()
		)
			.interpret
			.assertEqualTo(script("x" lineTo script("zero")))
	}

	@Test
	fun example() {
		script(
			"x" lineTo script("zero"),
			exampleName lineTo script("foo" lineTo script("bar"))
		)
			.interpret
			.assertEqualTo(script("x" lineTo script("zero")))
	}

	@Test
	fun valueSet() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one"),
				"x" lineTo script("two")
			),
			setName lineTo script(
				"y" lineTo script("three")
			)
		)
			.interpret
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("three"),
						"x" lineTo script("two")
					)
				)
			)

		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one"),
				"x" lineTo script("two")
			),
			setName lineTo script(
				"x" lineTo script("three"),
				"y" lineTo script("four")
			)
		)
			.interpret
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("four"),
						"x" lineTo script("three")
					)
				)
			)
	}

	@Test
	fun update() {
		script(
			"point" lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one"),
				"x" lineTo script("two")
			),
			updateName lineTo script(
				"y" lineTo script("done"),
				"x" lineTo script("updated")
			)
		)
			.interpret
			.assertEqualTo(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("done" lineTo script("one")),
						"x" lineTo script("updated" lineTo script("two"))
					)
				)
			)


	}
}
