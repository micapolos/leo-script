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
			line(literal("Hello, world!")),
			failName lineTo script())
			.expression
			.assertEqualTo(
				expression(
					op(literal("Hello, world!")),
					op(fail)))

		assertFailsWith<ValueError> {
			script(
				line(literal("Hello, world!")),
				failName lineTo script("foo"))
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
}