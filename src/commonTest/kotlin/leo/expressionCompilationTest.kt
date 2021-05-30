package leo

import leo.base.assertEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExpressionCompilationTest {
	@Test
	fun literal() {
		script(line(literal("Hello, world!")))
			.expression
			.assertEqualTo(expression(op(literal("Hello, world!"))))

		script(line(literal(123)))
			.expression
			.assertEqualTo(expression(op(literal(123))))
	}

	@Test
	fun as_() {
	 	script(
		  line(literal("Hello, world!")),
	    asName lineTo script(textName lineTo script(anyName)))
		  .expression
		  .assertEqualTo(
			  expression(
				  op(literal("Hello, world!")),
				  op(as_(pattern(script(textName lineTo script(anyName)))))))
	}

	@Test
	fun be() {
		script(
			line(literal("Hello, ")),
			beName lineTo script(line(literal("world!"))))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, ")),
					op(be(expression(op(literal("world!")))))))
	}

	@Test
	fun comment() {
		script(
			line(literal("Hello, world!")),
			commentName lineTo script("greeting"))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(comment(script("greeting")))))
	}

	@Test
	fun do_() {
		script(
			line(literal("Hello, world!")),
			doName lineTo script("length"))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(do_(expression("length" opTo expression())))))
	}

	@Test
	fun fail() {
		script(
			line(literal("Hello, ")),
			failName lineTo script(line(literal("boom!"))))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, ")),
					op(fail(expression(op(literal("boom!")))))))
	}

	@Test
	fun get() {
		script(
			line("point"),
			getName lineTo script(line("x"), line("y")))
			.expression
			.assertEqualTo(
				expression(
					op("point"),
					op(get("x", "y"))))
	}

	@Test
	fun letBe() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				beName lineTo script("pong")))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(let(pattern(script("ping")), be(expression("pong" opTo expression()))))))
	}

	@Test
	fun letDo() {
		script(
			line(literal("Hello, world!")),
			letName lineTo script(
				line("ping"),
				doName lineTo script("pong")))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(let(pattern(script("ping")), do_(expression("pong" opTo expression()))))))
	}

	@Test
	fun letError() {
		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				letName lineTo script())
				.expression
		}

		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				letName lineTo script("foo"))
				.expression
		}
	}

	@Test
	fun switch() {
		script(
			line(literal("Hello, world!")),
			switchName lineTo script(
				textName lineTo script(line(literal("text"))),
				numberName lineTo script(line(literal("number")))))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(switch(
						textName caseTo expression(op(literal("text"))),
						numberName caseTo expression(op(literal("number")))))))
	}

	@Test
	fun set() {
		script(
			line("point"),
			setName lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.expression
			.assertEqualTo(
				expression(
					op("point"),
					op(set(
						"x" fieldTo expression(op("zero")),
						"y" fieldTo expression(op("one"))))))
	}

	@Test
	fun try_() {
		script(
			line(literal("Hello, ")),
			tryName lineTo script(line(literal("boom!"))))
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, ")),
					op(try_(expression(op(literal("boom!")))))))
	}

	@Test
	fun update() {
		script(
			line("point"),
			updateName lineTo script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.expression
			.assertEqualTo(
				expression(
					op("point"),
					op(update(
						"x" fieldTo expression(op("zero")),
						"y" fieldTo expression(op("one"))))))
	}

	@Test
	fun use() {
		script(
			line("point"),
			useName lineTo script("lib" lineTo script("text")))
			.expression
			.assertEqualTo(
				expression(
					op("point"),
					op(use("lib", "text"))))
	}
}