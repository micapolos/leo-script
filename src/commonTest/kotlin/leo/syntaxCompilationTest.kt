package leo

import leo.base.assertEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SyntaxCompilationTest {
	@Test
	fun literal() {
		script(line(literal("Hello, world!")))
			.syntax
			.assertEqualTo(syntax(syntaxLine(literal("Hello, world!"))))

		script(line(literal(123)))
			.syntax
			.assertEqualTo(syntax(syntaxLine(literal(123))))
	}

	@Test
	fun as_() {
	 	script(
		  line(literal("Hello, world!")),
	    asName lineTo script(textName lineTo script(anyName)))
		  .syntax
		  .assertEqualTo(
			  syntax(
				  syntaxLine(literal("Hello, world!")),
				  line(as_(syntax(textName lineTo syntax(line(any())))))))
	}

	@Test
	fun be() {
		script(
			line(literal("Hello, ")),
			beName lineTo script(line(literal("world!"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, ")),
					line(be(syntax(syntaxLine(literal("world!")))))
				))
	}

	@Test
	fun bind() {
		script(
			line(literal("Hello, ")),
			bindName lineTo script(line(literal("world!"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, ")),
					line(bind(syntax(syntaxLine(literal("world!")))))
				))
	}

	@Test
	fun comment() {
		script(
			line(literal("Hello, world!")),
			commentName lineTo script("greeting"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(comment(script("greeting")))
				))
	}

	@Test
	fun do_() {
		script(
			line(literal("Hello, world!")),
			doName lineTo script("length"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(do_(block(syntax("length" lineTo syntax()))))
				))
	}

	@Test
	fun doing() {
		script(
			line(literal("Hello, world!")),
			doingName lineTo script("length"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(doing(block(syntax("length" lineTo syntax()))))
				))
	}

	@Test
	fun example() {
		script(
			line(literal("Hello, world!")),
			exampleName lineTo script("ok"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(example(syntax("ok" lineTo syntax())))
				))
	}

	@Test
	fun fail() {
		script(
			line(literal("Hello, ")),
			failName lineTo script(line(literal("boom!"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, ")),
					line(fail(syntax(syntaxLine(literal("boom!")))))
				))
	}

	@Test
	fun get() {
		script(
			line("point"),
			getName lineTo script(line("x"), line("y")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(get("x", "y"))
				))
	}

	@Test
	fun give() {
		script(
			line("point"),
			giveName lineTo script("x"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(give(syntax("x" lineTo syntax())))))
	}

	@Test
	fun is_() {
		script(
			line("point"),
			isName lineTo script(line("ok")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(syntax(syntaxLine("ok")))))))
	}

	@Test
	fun isNot() {
		script(
			line("point"),
			isName lineTo script(notName lineTo script(line("ok"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(syntax(syntaxLine("ok")))).negate)))
	}

	@Test
	fun isEqual() {
		script(
			line("point"),
			isName lineTo script(equalName lineTo script(line("ok"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(equal(syntax(syntaxLine("ok"))))))))
	}

	@Test
	fun isNotEqual() {
		script(
			line("point"),
			isName lineTo script(notName lineTo script(equalName lineTo script(line("ok")))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(equal(syntax(syntaxLine("ok"))))).negate)))
	}

	@Test
	fun isMatching() {
		script(
			line("point"),
			isName lineTo script(matchingName lineTo script(line("ok"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(matching(syntax("ok" lineTo syntax())))))))
	}

	@Test
	fun isNotMatching() {
		script(
			line("point"),
			isName lineTo script(notName lineTo script(matchingName lineTo script(line("ok")))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(is_(isRhs(matching(syntax("ok" lineTo syntax())))).negate)))
	}

	@Test
	fun letBe() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				beName lineTo script("pong")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(let(syntax("ping" lineTo syntax()), be(syntax("pong" lineTo syntax()))))
				))
	}

	@Test
	fun letDo() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				doName lineTo script("pong")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(let(syntax("ping" lineTo syntax()), do_(block(syntax("pong" lineTo syntax())))))
				))
	}

	@Test
	fun letDoRecursing() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				doName lineTo script(recursingName lineTo script("pong"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(let(syntax("ping" lineTo syntax()), do_(block(recursing(syntax("pong" lineTo syntax()))))))
				))
	}

	@Test
	fun letDoRepeating() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				doName lineTo script(repeatingName lineTo script("pong"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(let(syntax("ping" lineTo syntax()), do_(block(repeating(syntax("pong" lineTo syntax()))))))
				))
	}

	@Test
	fun letError() {
		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				letName lineTo script())
				.syntax
		}

		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				letName lineTo script("foo"))
				.syntax
		}
	}

	@Test
	fun matching() {
			script(
				line(literal("Hello, world!")),
				matchingName lineTo script(textName))
				.syntax
				.assertEqualTo(
					syntax(
						syntaxLine(literal("Hello, world!")),
						line(matching(syntax(textName lineTo syntax())))
					))
	}

	@Test
	fun switch() {
		script(
			line(literal("Hello, world!")),
			switchName lineTo script(
				textName lineTo script("foo"),
				numberName lineTo script("bar")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(switch(
						textName caseTo syntax("foo" lineTo syntax()),
						numberName caseTo syntax("bar" lineTo syntax())))
				))
	}

	@Test
	fun private() {
		script(
			line(literal("Hello, world!")),
			privateName lineTo script("foo"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(private(syntax("foo" lineTo syntax())))))
	}

	@Test
	fun recurse() {
		script(
			"foo" lineTo script(),
			recurseName lineTo script("bar"))
			.syntax
			.assertEqualTo(
				syntax(
					"foo" lineTo syntax(),
					line(recurse(syntax("bar")))))
	}

	@Test
	fun repeat() {
		script(
			"foo" lineTo script(),
			repeatName lineTo script("bar"))
			.syntax
			.assertEqualTo(
				syntax(
					"foo" lineTo syntax(),
					line(repeat(syntax("bar")))))
	}

	@Test
	fun quote() {
		script(
			line(literal("Hello, world!")),
			quoteName lineTo script("foo"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, world!")),
					line(quote(script("foo")))))
	}

	@Test
	fun switchCaseError() {
		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				switchName lineTo script(
					line(literal("foo")),
					line(literal(20))))
				.syntax
		}
	}

	@Test
	fun set() {
		script(
			line("point"),
			setName lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one"),
				line(literal("foo")),
				line(literal(123))))
			.syntax
			.assertEqualTo(
				syntax(
					"point" lineTo syntax(),
					line(set(
						atom("x" fieldTo syntax(syntaxLine("zero"))),
						atom("y" fieldTo syntax(syntaxLine("one"))),
						syntaxAtom(literal("foo")),
						syntaxAtom(literal(123))))
				))
	}

	@Test
	fun take() {
		script(
			line("point"),
			takeName lineTo script("x"))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(take(syntax("x" lineTo syntax())))))
	}

	@Test
	fun testIs() {
		script(
			"foo" lineTo script(),
			testName lineTo script(
				"zoo" lineTo script(),
				isName lineTo script("zar")))
			.syntax
			.assertEqualTo(
				syntax(
					"foo" lineTo syntax(),
					line(test(
						syntax("zoo" lineTo syntax()),
						is_(isRhs(syntax("zar" lineTo syntax())))))))
	}

	@Test
	fun testError() {
		assertFailsWith<ValueError> {
			script(
				"foo" lineTo script(),
				testName lineTo script("bar")
			)
				.syntax
		}
	}

	@Test
	fun try_() {
		script(
			line(literal("Hello, ")),
			tryName lineTo script(line(literal("boom!"))))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine(literal("Hello, ")),
					line(try_(syntax(syntaxLine(literal("boom!")))))
				))
	}

	@Test
	fun update() {
		script(
			line("point"),
			updateName lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(update(
						"x" fieldTo syntax(syntaxLine("zero")),
						"y" fieldTo syntax(syntaxLine("one"))))
				))
	}

	@Test
	fun use() {
		script(
			line("point"),
			useName lineTo script("lib" lineTo script("text")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(use("lib", "text"))
				))
	}

	@Test
	fun with() {
		script(
			line("point"),
			withName lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.syntax
			.assertEqualTo(
				syntax(
					syntaxLine("point"),
					line(with(syntax(
						"x" lineTo syntax("zero" lineTo syntax()),
						"y" lineTo syntax("one" lineTo syntax()))))))
	}

	@Test
	fun _any() {
		script(anyName).syntax.assertEqualTo(syntax(line(any())))
	}

	@Test
	fun loopBreak() {
		script(loopName lineTo script(breakName lineTo script("foo")))
			.syntax
			.assertEqualTo(
				syntax(line(loop(syntax(line(break_(syntax("foo")))))))
			)
	}

	@Test
	fun check() {
		script(
			line("foo"),
			checkName lineTo script("bar"))
			.syntax
			.assertEqualTo(
				syntax(
					"foo" lineTo syntax(),
					line(check(is_(isRhs(syntax("bar")))))))
	}
}